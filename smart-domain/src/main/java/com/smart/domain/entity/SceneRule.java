package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场景规则
 */
@Data
@TableName("t_scene_rule")
public class SceneRule {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long sceneId;

    private Integer conditionType;

    private Integer conditionSign;

    private String thresholdValue;

    private String action;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
