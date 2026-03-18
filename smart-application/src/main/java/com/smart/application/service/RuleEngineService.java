package com.smart.application.service;

import com.smart.common.enums.ConditionType;
import com.smart.common.enums.ValueSign;
import com.smart.domain.entity.SceneRule;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 规则引擎服务：条件匹配
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
public class RuleEngineService {

    /**
     * 判断设备数据是否满足所有规则条件
     */
    public boolean match(List<SceneRule> rules, DeviceDataMessage data) {
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        for (SceneRule rule : rules) {
            if (!matchSingle(rule, data)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchSingle(SceneRule rule, DeviceDataMessage data) {
        BigDecimal actualValue = getActualValue(rule.getConditionType(), data);
        if (actualValue == null) {
            return false;
        }
        BigDecimal threshold = new BigDecimal(rule.getThresholdValue());
        return compareValue(actualValue, threshold, rule.getConditionSign());
    }

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
            // TODO: 时间段条件判断
            return null;
        }
        log.warn("未知条件类型: {}", conditionType);
        return null;
    }

    private boolean compareValue(BigDecimal actual, BigDecimal threshold, Integer sign) {
        if (ValueSign.GT.getCode() == sign) {
            return actual.compareTo(threshold) > 0;
        } else if (ValueSign.GTE.getCode() == sign) {
            return actual.compareTo(threshold) >= 0;
        } else if (ValueSign.LT.getCode() == sign) {
            return actual.compareTo(threshold) < 0;
        } else if (ValueSign.LTE.getCode() == sign) {
            return actual.compareTo(threshold) <= 0;
        } else if (ValueSign.EQ.getCode() == sign) {
            return actual.compareTo(threshold) == 0;
        } else if (ValueSign.BETWEEN.getCode() == sign) {
            // TODO: BETWEEN 需要解析 thresholdValue 为两个值
            return false;
        }
        return false;
    }
}
