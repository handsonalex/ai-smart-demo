package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能场景实体类，对应MySQL数据库中的 t_smart_scene 表。
 * <p>
 * 智能场景是系统中自动化策略的核心抽象。一个场景定义了"在什么条件下执行什么操作"的业务逻辑，
 * 例如"当光伏发电功率超过负荷时自动储能"、"当电池SOC低于阈值时告警"等。
 * 每个场景关联一个电站，并包含一组规则（{@link SceneRule}）来定义具体的触发条件和执行动作。
 * 场景支持启用/禁用控制，以及优先级排序，当多个场景同时触发时按优先级执行。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_smart_scene")
public class SmartScene {

    /**
     * 场景唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 场景名称，如"削峰填谷"、"光伏自发自用"，用于界面展示和业务管理 */
    private String sceneName;

    /**
     * 场景类型编码。
     * <p>
     * 用于区分不同业务类别的场景，如：1-发电优化、2-储能管理、3-负荷调度、4-告警处理等。
     * 不同类型的场景对应不同的AI决策策略。
     * </p>
     */
    private Integer scenarioType;

    /** 所属电站ID，关联 {@link Station#id}，表示该场景作用于哪个电站 */
    private Long stationId;

    /**
     * 场景启用状态。
     * <p>
     * true-已启用，false-已禁用。
     * 禁用的场景不会参与规则匹配和自动决策，可用于临时暂停某个自动化策略。
     * </p>
     */
    private Boolean enabled;

    /**
     * 场景优先级，数值越小优先级越高。
     * <p>
     * 当同一电站下多个场景的规则同时被触发时，系统按优先级从高到低依次执行，
     * 避免冲突操作，确保决策的有序性。
     * </p>
     */
    private Integer priority;

    /** 场景描述信息，详细说明该场景的业务目的和适用条件，便于运维人员理解 */
    private String description;

    /**
     * 记录创建时间，仅在插入时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间，在插入和更新时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT_UPDATE} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
