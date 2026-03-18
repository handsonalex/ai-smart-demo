package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 值比较符号枚举
 *
 * @author smart
 */
@Getter
@AllArgsConstructor
public enum ValueSign {

    /**
     * 大于
     */
    GT(1, ">"),

    /**
     * 大于等于
     */
    GTE(2, ">="),

    /**
     * 小于
     */
    LT(3, "<"),

    /**
     * 小于等于
     */
    LTE(4, "<="),

    /**
     * 等于
     */
    EQ(5, "=="),

    /**
     * 介于
     */
    BETWEEN(6, "between");

    private final int code;

    private final String description;
}
