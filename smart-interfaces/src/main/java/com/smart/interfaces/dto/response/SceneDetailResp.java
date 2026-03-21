package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 场景详情响应 DTO
 *
 * <p>用于向前端返回场景的完整信息，包括场景基本属性和关联的规则列表。
 * 该 DTO 体现了场景与规则的聚合关系（一对多），便于前端一次请求获取完整的场景配置。
 *
 * <p>场景类型枚举：
 * <ul>
 *   <li>1 - 削峰填谷</li>
 *   <li>2 - 需量控制</li>
 *   <li>3 - 光伏自消纳</li>
 *   <li>4 - 应急备电</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "场景详情响应")
public class SceneDetailResp {
    /** 场景主键 ID */
    @Schema(description = "场景ID")
    private Long id;

    /** 场景名称 */
    @Schema(description = "场景名称")
    private String sceneName;

    /** 场景类型：1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电 */
    @Schema(description = "场景类型")
    private Integer scenarioType;

    /** 所属电站 ID */
    @Schema(description = "电站ID")
    private Long stationId;

    /** 是否启用，关闭后该场景不参与决策匹配 */
    @Schema(description = "是否启用")
    private Boolean enabled;

    /** 优先级，数值越大优先级越高 */
    @Schema(description = "优先级")
    private Integer priority;

    /** 场景描述 */
    @Schema(description = "描述")
    private String description;

    /** 关联的规则列表，定义了该场景的触发条件和执行动作 */
    @Schema(description = "规则列表")
    private List<RuleResp> rules;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 规则响应 DTO —— 场景详情中内嵌的规则信息
     *
     * <p>每条规则包含条件定义（conditionType + conditionSign + thresholdValue）
     * 和动作定义（action），用于描述"当满足什么条件时执行什么操作"。
     */
    @Data
    @Schema(description = "规则响应")
    public static class RuleResp {
        /** 规则主键 ID */
        private Long id;
        /** 条件类型：1-SOC 2-功率 3-电压 4-温度 5-时间段 */
        private Integer conditionType;
        /** 比较运算符：1-> 2->= 3-< 4-<= 5-== 6-between */
        private Integer conditionSign;
        /** 阈值（如"80"表示 SOC 80%，"20,80"表示区间） */
        private String thresholdValue;
        /** 动作指令（如"CHARGE"、"DISCHARGE"、"STOP"） */
        private String action;
    }
}
