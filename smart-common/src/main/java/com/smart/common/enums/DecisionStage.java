package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 决策阶段枚举
 * <p>
 * 定义决策引擎执行决策时所经历的各个阶段。一次完整的决策流程按顺序包含：
 * 规则匹配 -> RAG 检索 -> AI 推理 -> 指令下发。
 * </p>
 * <p>
 * 设计说明：系统采用"规则引擎 + RAG + AI"的三层决策架构：
 * <ul>
 *   <li>第一层：规则匹配 —— 基于预定义的阈值规则快速判断</li>
 *   <li>第二层：RAG 检索 —— 从知识库中检索相似历史案例和专家经验</li>
 *   <li>第三层：AI 推理 —— 结合实时数据和 RAG 结果，由大模型进行智能决策</li>
 *   <li>第四层：指令下发 —— 将最终决策转化为设备控制指令并下发执行</li>
 * </ul>
 * 通过阶段枚举可以精确追踪决策链路，便于问题定位和性能分析。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DecisionStage {

    /**
     * 规则匹配 —— 决策的第一阶段，根据预配置的条件规则对设备数据进行匹配判断
     */
    RULE_MATCH(1, "规则匹配"),

    /**
     * RAG 检索 —— 决策的第二阶段，从向量知识库中检索相关的历史案例和运维经验
     */
    RAG_RETRIEVAL(2, "RAG检索"),

    /**
     * AI 推理 —— 决策的第三阶段，将实时数据和 RAG 检索结果输入大语言模型进行智能推理
     */
    AI_INFERENCE(3, "AI推理"),

    /**
     * 指令下发 —— 决策的最终阶段，将 AI 生成的决策结果转化为具体的设备控制指令并下发
     */
    COMMAND_DISPATCH(4, "指令下发");

    /** 决策阶段编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 决策阶段的中文描述，用于前端展示和日志输出 */
    private final String description;
}
