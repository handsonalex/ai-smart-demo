package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备数据
 */
@Data
@TableName("t_device_data")
public class DeviceData {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long deviceId;

    private Long stationId;

    private BigDecimal power;

    private BigDecimal voltage;

    private BigDecimal current;

    private BigDecimal temperature;

    private BigDecimal soc;

    private LocalDateTime collectTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
