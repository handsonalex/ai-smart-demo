package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 决策查询请求 DTO
 *
 * <p>用于接收前端查询决策记录时的筛选条件和分页参数。
 * 所有筛选条件均为可选，支持按电站、场景、状态维度组合查询。
 *
 * <p>决策状态枚举：
 * <ul>
 *   <li>0 - 待处理：决策已创建，尚未开始执行</li>
 *   <li>1 - 执行中：决策正在执行（如正在调用 AI 推理或下发指令）</li>
 *   <li>2 - 已完成：决策执行成功</li>
 *   <li>3 - 失败：决策执行过程中出现异常</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "决策查询请求")
public class DecisionQueryReq {
    /** 电站 ID，选填，按电站维度筛选决策记录 */
    @Schema(description = "电站ID")
    private Long stationId;

    /** 场景 ID，选填，按场景维度筛选决策记录 */
    @Schema(description = "场景ID")
    private Long sceneId;

    /** 决策状态，选填，取值范围：0-待处理 1-执行中 2-已完成 3-失败 */
    @Schema(description = "状态: 0-待处理 1-执行中 2-已完成 3-失败")
    private Integer status;

    /** 页码，默认为 1，用于分页查询 */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /** 每页大小，默认为 10，控制单次查询返回的记录数 */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
