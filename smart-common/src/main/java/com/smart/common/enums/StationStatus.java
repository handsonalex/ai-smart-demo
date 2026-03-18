package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电站状态枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum StationStatus {

    /**
     * 离线
     */
    OFFLINE(0, "离线"),

    /**
     * 在线
     */
    ONLINE(1, "在线"),

    /**
     * 告警
     */
    ALARM(2, "告警");

    private final int code;

    private final String description;
}
