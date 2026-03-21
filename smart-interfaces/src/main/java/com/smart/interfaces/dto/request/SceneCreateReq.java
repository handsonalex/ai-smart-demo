package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 创建场景请求 DTO
 *
 * <p>用于接收前端创建智慧能源场景时提交的参数。场景是决策引擎的核心配置单元，
 * 每个场景定义了一组规则（条件→动作），当设备数据满足条件时自动触发相应动作。
 *
 * <p>场景类型枚举：
 * <ul>
 *   <li>1 - 削峰填谷：在电价低谷期充电、高峰期放电，降低用电成本</li>
 *   <li>2 - 需量控制：控制用电需量不超过合同约定值，避免罚款</li>
 *   <li>3 - 光伏自消纳：优先使用光伏发电，减少向电网购电</li>
 *   <li>4 - 应急备电：在电网故障时自动切换到储能供电，保障关键负荷</li>
 * </ul>
 *
 * <p>场景与规则是一对多的聚合关系，创建场景时可同时提交规则列表。
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建场景请求")
public class SceneCreateReq {
    /** 场景名称，必填，用于标识场景（如"工厂A削峰填谷策略"） */
    @NotBlank(message = "场景名称不能为空")
    @Schema(description = "场景名称")
    private String sceneName;

    /** 场景类型，必填，取值范围：1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电 */
    @NotNull(message = "场景类型不能为空")
    @Schema(description = "场景类型: 1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电")
    private Integer scenarioType;

    /** 所属电站 ID，必填，场景必须绑定到具体的电站 */
    @NotNull(message = "电站ID不能为空")
    @Schema(description = "电站ID")
    private Long stationId;

    /** 是否启用，默认为 true。关闭后场景规则不参与决策匹配 */
    @Schema(description = "是否启用")
    private Boolean enabled = true;

    /** 优先级，默认为 0。当多个场景同时匹配时，优先级高的场景优先执行 */
    @Schema(description = "优先级")
    private Integer priority = 0;

    /** 场景描述，选填，对场景用途的补充说明 */
    @Schema(description = "场景描述")
    private String description;

    /** 规则列表，选填，场景关联的规则集合，定义"在什么条件下执行什么动作" */
    @Schema(description = "规则列表")
    private List<RuleItem> rules;

    /**
     * 规则项 —— 场景创建请求中的内嵌规则 DTO
     *
     * <p>每条规则由"条件"和"动作"两部分组成：
     * <ul>
     *   <li>条件 = 条件类型 + 比较符 + 阈值（如：SOC > 80%）</li>
     *   <li>动作 = 满足条件时执行的指令（如：停止充电）</li>
     * </ul>
     */
    @Data
    @Schema(description = "规则项")
    public static class RuleItem {
        /** 条件类型：1-SOC（电池荷电状态） 2-功率 3-电压 4-温度 5-时间段 */
        @Schema(description = "条件类型: 1-SOC 2-功率 3-电压 4-温度 5-时间段")
        private Integer conditionType;
        /** 比较运算符：1-大于 2-大于等于 3-小于 4-小于等于 5-等于 6-区间（between） */
        @Schema(description = "比较符: 1-> 2->= 3-< 4-<= 5-== 6-between")
        private Integer conditionSign;
        /** 阈值，字符串类型以支持多种格式（如单值"80"或区间"20,80"） */
        @Schema(description = "阈值")
        private String thresholdValue;
        /** 动作指令，当条件满足时执行的操作（如"CHARGE"、"DISCHARGE"、"STOP"） */
        @Schema(description = "动作指令")
        private String action;
    }
}
