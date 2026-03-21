package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场景规则实体类，对应MySQL数据库中的 t_scene_rule 表。
 * <p>
 * 场景规则是智能场景（{@link SmartScene}）下的具体触发条件定义。
 * 每条规则描述了一个"条件-动作"对：当设备上报的数据满足指定条件时，触发对应的动作。
 * 例如："当温度 > 60℃ 时执行告警"、"当SOC < 20% 时执行停止放电"。
 * 一个智能场景可以包含多条规则，规则之间可以是"与"或"或"的逻辑关系（由上层业务决定）。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_scene_rule")
public class SceneRule {

    /**
     * 规则唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属智能场景ID，关联 {@link SmartScene#id}，表示该规则属于哪个场景 */
    private Long sceneId;

    /**
     * 条件类型编码，指定要比较的数据指标。
     * <p>
     * 典型取值：1-功率(power)、2-电压(voltage)、3-电流(current)、4-温度(temperature)、5-电池SOC。
     * 系统根据此字段从设备上报数据中提取对应的指标值进行规则匹配。
     * </p>
     */
    private Integer conditionType;

    /**
     * 条件比较运算符编码。
     * <p>
     * 典型取值：1-大于(>)、2-小于(<)、3-等于(=)、4-大于等于(>=)、5-小于等于(<=)。
     * 与 conditionType 和 thresholdValue 配合，构成完整的条件表达式。
     * </p>
     */
    private Integer conditionSign;

    /**
     * 条件阈值（字符串类型，支持灵活配置）。
     * <p>
     * 例如："60"表示温度阈值60℃，"20"表示SOC阈值20%。
     * 使用字符串类型存储是为了兼容不同类型的阈值（数值型、百分比、枚举值等）。
     * </p>
     */
    private String thresholdValue;

    /**
     * 规则触发后执行的动作描述。
     * <p>
     * 例如："send_alarm"（发送告警）、"stop_discharge"（停止放电）、"start_charge"（启动充电）。
     * 动作字符串由上层业务模块解析并执行具体操作。
     * </p>
     */
    private String action;

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
