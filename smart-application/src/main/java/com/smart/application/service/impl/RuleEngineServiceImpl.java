package com.smart.application.service.impl;

import com.smart.application.service.RuleEngineService;
import com.smart.common.enums.ConditionType;
import com.smart.common.enums.ValueSign;
import com.smart.domain.entity.SceneRule;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 规则引擎服务 —— 负责判断设备数据是否满足场景触发条件
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层</b>，封装了规则匹配的核心算法逻辑。
 * 它被 {@link DecisionAppServiceImpl} 在决策流程的第一阶段（规则匹配）调用。</p>
 *
 * <h3>匹配算法说明</h3>
 * <ul>
 *   <li><b>AND 逻辑</b>：一个场景关联的所有规则必须全部满足，场景才算匹配成功（短路求值：任一规则不满足即返回 false）</li>
 *   <li><b>单条规则匹配</b>：从设备数据中提取条件类型对应的实际值，与规则阈值按指定比较符进行比较</li>
 *   <li><b>条件类型</b>：支持 SOC（电池荷电状态）、功率、电压、温度等，通过 ConditionType 枚举映射</li>
 *   <li><b>比较运算符</b>：支持 GT（大于）、GTE（大于等于）、LT（小于）、LTE（小于等于）、EQ（等于）、BETWEEN（区间），通过 ValueSign 枚举映射</li>
 * </ul>
 *
 * <h3>示例</h3>
 * <pre>
 * 场景"高温告警"的规则：
 *   规则1: 温度 > 60°C    (conditionType=TEMPERATURE, sign=GT, threshold=60)
 *   规则2: 功率 > 500kW    (conditionType=POWER, sign=GT, threshold=500)
 * → 设备数据 temperature=65, power=520 → 两条规则都满足 → 匹配成功
 * → 设备数据 temperature=65, power=400 → 规则2不满足 → 匹配失败
 * </pre>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
public class RuleEngineServiceImpl implements RuleEngineService {

    /**
     * 判断设备数据是否满足所有规则条件（AND 逻辑）
     *
     * <p>遍历规则列表，逐条进行匹配。采用短路求值策略：
     * 一旦某条规则不满足，立即返回 false，不再检查剩余规则，提升匹配效率。</p>
     *
     * @param rules 场景关联的规则列表（一个场景可配置多条规则）
     * @param data  设备上报的数据消息
     * @return true=所有规则都满足（场景匹配成功），false=至少有一条规则不满足或规则为空
     */
    @Override
    public boolean match(List<SceneRule> rules, DeviceDataMessage data) {
        // 空规则列表视为不匹配，防止无条件场景被误触发
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        // AND 逻辑：所有规则必须全部通过
        for (SceneRule rule : rules) {
            if (!matchSingle(rule, data)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 匹配单条规则：提取实际值 → 解析阈值 → 比较
     *
     * @param rule 单条场景规则，包含条件类型、比较符、阈值
     * @param data 设备上报数据
     * @return true=该条规则满足，false=不满足
     */
    private boolean matchSingle(SceneRule rule, DeviceDataMessage data) {
        // 第一步：根据规则的条件类型（如温度、功率等），从设备数据中提取对应的实际值
        BigDecimal actualValue = getActualValue(rule.getConditionType(), data);
        if (actualValue == null) {
            return false;
        }
        // 第二步：将规则中的阈值字符串转换为 BigDecimal，确保精确比较
        BigDecimal threshold = new BigDecimal(rule.getThresholdValue());
        // 第三步：按规则指定的比较符（>、>=、<、<=、==、BETWEEN）进行数值比较
        return compareValue(actualValue, threshold, rule.getConditionSign());
    }

    /**
     * 根据条件类型从设备数据中提取对应的实际数值
     *
     * <p>本质上是一个条件类型到设备属性的映射表。
     * 当需要新增监控指标时，只需在此方法中添加对应的 if 分支和 ConditionType 枚举值。</p>
     *
     * @param conditionType 条件类型编码（对应 ConditionType 枚举的 code 值）
     * @param data          设备上报数据
     * @return 对应的实际数值，未知类型返回 null（即该条规则不匹配）
     */
    private BigDecimal getActualValue(Integer conditionType, DeviceDataMessage data) {
        if (ConditionType.SOC.getCode() == conditionType) {
            return data.getSoc();
        } else if (ConditionType.POWER.getCode() == conditionType) {
            return data.getPower();
        } else if (ConditionType.VOLTAGE.getCode() == conditionType) {
            return data.getVoltage();
        } else if (ConditionType.TEMPERATURE.getCode() == conditionType) {
            return data.getTemperature();
        } else if (ConditionType.TIME_RANGE.getCode() == conditionType) {
            // TODO: 时间段条件判断，需要将当前时间转换为可比较的数值或采用独立的时间匹配逻辑
            return null;
        }
        log.warn("未知条件类型: {}", conditionType);
        return null;
    }

    /**
     * 执行数值比较运算
     *
     * <p>使用 {@link BigDecimal#compareTo} 进行精确数值比较，避免浮点数精度问题。
     * 支持六种比较运算符，通过 ValueSign 枚举统一管理。</p>
     *
     * @param actual    设备上报的实际值
     * @param threshold 规则配置的阈值
     * @param sign      比较运算符编码（对应 ValueSign 枚举的 code 值）
     * @return true=比较条件成立，false=不成立或运算符未知
     */
    private boolean compareValue(BigDecimal actual, BigDecimal threshold, Integer sign) {
        if (ValueSign.GT.getCode() == sign) {
            // 大于：actual > threshold
            return actual.compareTo(threshold) > 0;
        } else if (ValueSign.GTE.getCode() == sign) {
            // 大于等于：actual >= threshold
            return actual.compareTo(threshold) >= 0;
        } else if (ValueSign.LT.getCode() == sign) {
            // 小于：actual < threshold
            return actual.compareTo(threshold) < 0;
        } else if (ValueSign.LTE.getCode() == sign) {
            // 小于等于：actual <= threshold
            return actual.compareTo(threshold) <= 0;
        } else if (ValueSign.EQ.getCode() == sign) {
            // 等于：actual == threshold
            return actual.compareTo(threshold) == 0;
        } else if (ValueSign.BETWEEN.getCode() == sign) {
            // TODO: BETWEEN 需要解析 thresholdValue 为两个值（如 "10,90"），分别作为下界和上界进行范围判断
            return false;
        }
        return false;
    }
}
