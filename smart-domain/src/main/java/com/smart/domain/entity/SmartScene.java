package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能场景
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_smart_scene")
public class SmartScene {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String sceneName;

    private Integer scenarioType;

    private Long stationId;

    private Boolean enabled;

    private Integer priority;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
