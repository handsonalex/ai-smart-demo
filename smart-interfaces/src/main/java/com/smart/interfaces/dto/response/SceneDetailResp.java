package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 场景详情响应
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "场景详情响应")
public class SceneDetailResp {
    @Schema(description = "场景ID")
    private Long id;
    @Schema(description = "场景名称")
    private String sceneName;
    @Schema(description = "场景类型")
    private Integer scenarioType;
    @Schema(description = "电站ID")
    private Long stationId;
    @Schema(description = "是否启用")
    private Boolean enabled;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "规则列表")
    private List<RuleResp> rules;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Data
    @Schema(description = "规则响应")
    public static class RuleResp {
        private Long id;
        private Integer conditionType;
        private Integer conditionSign;
        private String thresholdValue;
        private String action;
    }
}
