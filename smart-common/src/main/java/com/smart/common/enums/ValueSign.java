package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 值比较符号枚举
 * <p>
 * 定义规则条件中用于数值比较的运算符。在规则匹配阶段，系统根据
 * {@link ConditionType} 提取设备指标值后，使用此枚举定义的比较符号
 * 与阈值进行比较，判断规则条件是否成立。
 * </p>
 * <p>
 * 设计说明：将比较运算符枚举化，使得规则条件可以完全通过数据库配置来定义，
 * 实现"条件类型 + 比较符号 + 阈值"的灵活规则表达式，例如：SOC > 80、温度 BETWEEN 20~45。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum ValueSign {

    /**
     * 大于 —— 判断指标值是否严格大于设定阈值
     */
    GT(1, ">"),

    /**
     * 大于等于 —— 判断指标值是否大于或等于设定阈值
     */
    GTE(2, ">="),

    /**
     * 小于 —— 判断指标值是否严格小于设定阈值
     */
    LT(3, "<"),

    /**
     * 小于等于 —— 判断指标值是否小于或等于设定阈值
     */
    LTE(4, "<="),

    /**
     * 等于 —— 判断指标值是否精确等于设定阈值
     */
    EQ(5, "=="),

    /**
     * 介于 —— 判断指标值是否处于两个阈值之间（闭区间），
     * 需要同时配置上限和下限两个阈值
     */
    BETWEEN(6, "between");

    /** 比较符号编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 比较符号的字符串表示，用于规则表达式的展示和日志输出 */
    private final String description;
}
