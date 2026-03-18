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
 * 电站
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_station")
public class Station {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String stationName;

    private String location;

    private BigDecimal capacity;

    private Integer status;

    private String contactPerson;

    private String contactPhone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
