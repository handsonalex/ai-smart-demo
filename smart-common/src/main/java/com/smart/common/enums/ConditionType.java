package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则条件类型枚举
 *
 * @author smart
 */
@Getter
@AllArgsConstructor
public enum ConditionType {

    /**
     * SOC（电池荷电状态）
     */
    SOC(1, "SOC"),

    /**
     * 功率
     */
    POWER(2, "功率"),

    /**
     * 电压
     */
    VOLTAGE(3, "电压"),

    /**
     * 温度
     */
    TEMPERATURE(4, "温度"),

    /**
     * 时间段
     */
    TIME_RANGE(5, "时间段");

    private final int code;

    private final String description;
}
