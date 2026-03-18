package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 创建场景请求
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建场景请求")
public class SceneCreateReq {
    @NotBlank(message = "场景名称不能为空")
    @Schema(description = "场景名称")
    private String sceneName;

    @NotNull(message = "场景类型不能为空")
    @Schema(description = "场景类型: 1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电")
    private Integer scenarioType;

    @NotNull(message = "电站ID不能为空")
    @Schema(description = "电站ID")
    private Long stationId;

    @Schema(description = "是否启用")
    private Boolean enabled = true;

    @Schema(description = "优先级")
    private Integer priority = 0;

    @Schema(description = "场景描述")
    private String description;

    @Schema(description = "规则列表")
    private List<RuleItem> rules;

    @Data
    @Schema(description = "规则项")
    public static class RuleItem {
        @Schema(description = "条件类型: 1-SOC 2-功率 3-电压 4-温度 5-时间段")
        private Integer conditionType;
        @Schema(description = "比较符: 1-> 2->= 3-< 4-<= 5-== 6-between")
        private Integer conditionSign;
        @Schema(description = "阈值")
        private String thresholdValue;
        @Schema(description = "动作指令")
        private String action;
    }
}
