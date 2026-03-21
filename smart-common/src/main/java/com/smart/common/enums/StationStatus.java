package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电站状态枚举
 * <p>
 * 定义电站（储能站/光伏电站）的运行状态。电站是系统中管理设备的最上层实体，
 * 一个电站下包含多个设备，电站状态反映其整体运行健康度。
 * </p>
 * <p>
 * 设计说明：电站状态用于监控大屏展示和告警通知。
 * 状态流转规则：OFFLINE <-> ONLINE <-> ALARM，其中告警状态通常由设备异常触发。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum StationStatus {

    /**
     * 离线 —— 电站与系统之间的通信中断，无法获取实时数据
     */
    OFFLINE(0, "离线"),

    /**
     * 在线 —— 电站正常运行，所有设备通信正常，数据实时上报
     */
    ONLINE(1, "在线"),

    /**
     * 告警 —— 电站存在异常（如设备故障、指标超限等），需要运维人员关注
     */
    ALARM(2, "告警");

    /** 电站状态编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 电站状态的中文描述，用于前端展示和日志输出 */
    private final String description;
}
