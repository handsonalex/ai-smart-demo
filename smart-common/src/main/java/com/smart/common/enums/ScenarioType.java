package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景类型枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum ScenarioType {

    /**
     * 削峰填谷
     */
    PEAK_SHAVING(1, "削峰填谷"),

    /**
     * 需量控制
     */
    DEMAND_CONTROL(2, "需量控制"),

    /**
     * 光伏自消纳
     */
    SELF_CONSUMPTION(3, "光伏自消纳"),

    /**
     * 应急备电
     */
    EMERGENCY_BACKUP(4, "应急备电");

    private final int code;

    private final String description;
}
