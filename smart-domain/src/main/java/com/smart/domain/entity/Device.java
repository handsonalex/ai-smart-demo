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
 * 设备
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_device")
public class Device {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long stationId;

    private String deviceName;

    private Integer deviceType;

    private String deviceSn;

    private BigDecimal ratedPower;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
