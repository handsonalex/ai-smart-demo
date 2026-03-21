package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则条件类型枚举
 * <p>
 * 定义决策规则中可使用的条件维度。在规则匹配阶段，系统会根据条件类型
 * 从设备上报数据中提取对应的指标值，结合 {@link ValueSign} 比较符号进行阈值判断。
 * </p>
 * <p>
 * 设计说明：条件类型与设备上报的数据字段一一对应，
 * 支持通过配置化方式灵活组合规则条件，无需修改代码即可新增或调整决策规则。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum ConditionType {

    /**
     * SOC（State of Charge，电池荷电状态） —— 表示电池剩余电量百分比，
     * 是储能电池充放电决策的核心指标，取值范围 0%-100%
     */
    SOC(1, "SOC"),

    /**
     * 功率 —— 设备的实时输出/输入功率（单位：kW），
     * 用于需量控制和负载均衡等场景的决策判断
     */
    POWER(2, "功率"),

    /**
     * 电压 —— 设备端的实时电压值（单位：V），
     * 用于设备安全保护和电能质量监控
     */
    VOLTAGE(3, "电压"),

    /**
     * 温度 —— 设备运行温度（单位：摄氏度），
     * 用于设备过温保护，防止电池热失控等安全隐患
     */
    TEMPERATURE(4, "温度"),

    /**
     * 时间段 —— 基于时间维度的条件判断，
     * 用于削峰填谷场景中区分电价峰谷时段，触发对应的充放电策略
     */
    TIME_RANGE(5, "时间段");

    /** 条件类型编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 条件类型的中文描述，用于前端展示和日志输出 */
    private final String description;
}
