package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "决策详情响应")
public class DecisionDetailResp {
    @Schema(description = "决策ID")
    private Long id;
    @Schema(description = "电站ID")
    private Long stationId;
    @Schema(description = "场景ID")
    private Long sceneId;
    @Schema(description = "触发数据")
    private String triggerData;
    @Schema(description = "决策结果")
    private String decisionResult;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "阶段")
    private Integer stage;
    @Schema(description = "AI响应")
    private String aiResponse;
    @Schema(description = "执行时间")
    private LocalDateTime executedAt;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
