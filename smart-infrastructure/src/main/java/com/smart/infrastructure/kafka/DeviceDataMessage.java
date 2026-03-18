package com.smart.infrastructure.kafka;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备数据消息体
 *
 * @author Joseph Ho
 */
@Data
public class DeviceDataMessage implements Serializable {
    private Long deviceId;
    private Long stationId;
    private BigDecimal power;
    private BigDecimal voltage;
    private BigDecimal current;
    private BigDecimal temperature;
    private BigDecimal soc;
    private LocalDateTime collectTime;
}
