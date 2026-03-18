package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * 光伏板
     */
    PV_PANEL(1, "光伏板"),

    /**
     * 储能电池
     */
    BATTERY(2, "储能电池"),

    /**
     * 逆变器
     */
    INVERTER(3, "逆变器"),

    /**
     * 电表
     */
    METER(4, "电表");

    private final int code;

    private final String description;
}
