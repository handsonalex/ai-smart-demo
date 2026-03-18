package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 决策阶段枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DecisionStage {

    /**
     * 规则匹配
     */
    RULE_MATCH(1, "规则匹配"),

    /**
     * RAG 检索
     */
    RAG_RETRIEVAL(2, "RAG检索"),

    /**
     * AI 推理
     */
    AI_INFERENCE(3, "AI推理"),

    /**
     * 指令下发
     */
    COMMAND_DISPATCH(4, "指令下发");

    private final int code;

    private final String description;
}
