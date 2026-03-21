package com.smart.infrastructure.kafka;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备数据消息体 DTO（Data Transfer Object）
 *
 * <p>用于 Kafka 消息传输的设备数据结构，封装了 IoT 设备上报的实时采集数据。
 * 该消息由 IoT 网关序列化为 JSON 发送到 Kafka，再由 {@link DeviceDataConsumer} 反序列化消费。</p>
 *
 * <p>实现 {@link Serializable} 接口以支持序列化传输。</p>
 *
 * @author Joseph Ho
 */
@Data
public class DeviceDataMessage implements Serializable {

    /** 设备唯一标识 */
    private Long deviceId;

    /** 设备所属电站 ID */
    private Long stationId;

    /** 功率（单位：kW） */
    private BigDecimal power;

    /** 电压（单位：V） */
    private BigDecimal voltage;

    /** 电流（单位：A） */
    private BigDecimal current;

    /** 温度（单位：摄氏度） */
    private BigDecimal temperature;

    /** 电池荷电状态 SOC（State of Charge，单位：百分比） */
    private BigDecimal soc;

    /** 数据采集时间 */
    private LocalDateTime collectTime;
}
