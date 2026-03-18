package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策记录
 */
@Data
@TableName("t_decision_record")
public class DecisionRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long stationId;

    private Long sceneId;

    private String triggerData;

    private String decisionResult;

    private Integer status;

    private Integer stage;

    private String aiResponse;

    private LocalDateTime executedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
