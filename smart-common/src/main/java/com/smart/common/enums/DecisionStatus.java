package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 决策状态枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DecisionStatus {

    /**
     * 待处理
     */
    PENDING(0, "待处理"),

    /**
     * 执行中
     */
    EXECUTING(1, "执行中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败
     */
    FAILED(3, "失败");

    private final int code;

    private final String description;
}
