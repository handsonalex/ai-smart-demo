package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 决策查询请求
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "决策查询请求")
public class DecisionQueryReq {
    @Schema(description = "电站ID")
    private Long stationId;
    @Schema(description = "场景ID")
    private Long sceneId;
    @Schema(description = "状态: 0-待处理 1-执行中 2-已完成 3-失败")
    private Integer status;
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
