package com.smart.infrastructure.log;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 决策日志实体 —— Elasticsearch 文档结构
 *
 * <p>本类定义了存储到 Elasticsearch 中的决策日志文档结构。每条日志记录决策过程中
 * 某个阶段的详细信息，用于实现决策过程的全链路追踪和可观测性。</p>
 *
 * <p>一次完整的决策流程会产生多条日志，每条日志对应一个阶段（stage），例如：</p>
 * <ul>
 *   <li>rule_match：规则匹配阶段</li>
 *   <li>ai_inference：AI 模型推理阶段</li>
 *   <li>result_output：结果输出阶段</li>
 * </ul>
 *
 * <p>通过 ES 的聚合查询能力，可以实现：</p>
 * <ul>
 *   <li>按电站、场景维度统计决策成功率</li>
 *   <li>分析各阶段的耗时分布，发现性能瓶颈</li>
 *   <li>快速定位决策失败的原因</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
public class DecisionLog {

    /** ES 文档 ID（通常使用 UUID 自动生成） */
    private String id;

    /** 关联的决策记录 ID，用于将同一次决策的多个阶段日志关联在一起 */
    private Long decisionId;

    /** 电站 ID，支持按电站维度查询和聚合分析 */
    private Long stationId;

    /** 场景 ID，支持按场景维度查询和聚合分析 */
    private Long sceneId;

    /** 决策阶段标识（如 rule_match、ai_inference、result_output 等） */
    private String stage;

    /** 该阶段的输入数据（JSON 格式），便于问题排查和决策回溯 */
    private String input;

    /** 该阶段的输出数据（JSON 格式），记录每个阶段的处理结果 */
    private String output;

    /** 该阶段的执行耗时（毫秒），用于性能分析和瓶颈定位 */
    private Long costMs;

    /** 该阶段是否执行成功 */
    private Boolean success;

    /** 失败时的错误信息，用于快速定位问题 */
    private String errorMsg;

    /** 日志创建时间 */
    private LocalDateTime createTime;
}
