package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 决策日志响应 DTO
 *
 * <p>用于向前端返回决策执行过程中的详细日志。每条日志对应决策流程中的一个阶段，
 * 记录了该阶段的输入、输出、耗时和执行结果，便于问题排查和性能分析。
 *
 * <p>典型的决策阶段包括：规则匹配、知识检索（RAG）、AI推理、指令生成、指令下发等。
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "决策日志响应")
public class DecisionLogResp {
    /** 日志唯一标识（字符串类型，可能使用 UUID 或 MongoDB ObjectId） */
    @Schema(description = "日志ID")
    private String id;

    /** 关联的决策记录 ID，标识该日志属于哪次决策 */
    @Schema(description = "决策ID")
    private Long decisionId;

    /** 决策阶段名称（如"RULE_MATCH"、"AI_INFERENCE"、"COMMAND_SEND"） */
    @Schema(description = "阶段")
    private String stage;

    /** 该阶段的输入数据，JSON 格式 */
    @Schema(description = "输入")
    private String input;

    /** 该阶段的输出数据，JSON 格式 */
    @Schema(description = "输出")
    private String output;

    /** 该阶段的执行耗时（单位：毫秒），用于性能监控 */
    @Schema(description = "耗时(ms)")
    private Long costMs;

    /** 该阶段是否执行成功 */
    @Schema(description = "是否成功")
    private Boolean success;

    /** 失败时的错误信息，成功时为 null */
    @Schema(description = "错误信息")
    private String errorMsg;

    /** 日志创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
