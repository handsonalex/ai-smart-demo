package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型枚举
 * <p>
 * 定义智慧能源系统中所支持的设备类型。每种设备类型对应一个唯一编码和中文描述。
 * 在设备注册、数据上报、决策规则匹配等环节中，通过此枚举区分不同类型的硬件设备。
 * </p>
 * <p>
 * 设计说明：使用整型 code 作为持久化标识，避免将枚举名称直接存储到数据库中，
 * 便于数据库查询和前后端数据交互。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * 光伏板 —— 太阳能发电的核心组件，负责将太阳能转化为直流电能
     */
    PV_PANEL(1, "光伏板"),

    /**
     * 储能电池 —— 用于存储电能，实现削峰填谷、应急备电等功能
     */
    BATTERY(2, "储能电池"),

    /**
     * 逆变器 —— 将光伏板产生的直流电转换为交流电，供电网或负载使用
     */
    INVERTER(3, "逆变器"),

    /**
     * 电表 —— 用于计量电能的输入和输出，是电量统计和结算的基础设备
     */
    METER(4, "电表");

    /** 设备类型编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 设备类型的中文描述，用于前端展示和日志输出 */
    private final String description;
}
