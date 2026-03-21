package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI决策记录实体类，对应MySQL数据库中的 t_decision_record 表。
 * <p>
 * 该实体完整记录了系统每一次AI决策的全过程，包括触发数据、决策结果、AI响应内容以及执行状态。
 * 决策记录是系统可追溯性和可审计性的重要保障，运维人员可以通过查看决策记录了解系统的自动化行为。
 * </p>
 * <p>
 * 一次典型的决策流程为：设备数据触发场景规则 -> 系统调用AI模型分析 -> 生成决策结果 -> 执行动作 -> 记录结果。
 * stage字段支持多阶段决策场景，方便追踪决策所处的阶段。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_decision_record")
public class DecisionRecord {

    /**
     * 决策记录唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 关联电站ID，关联 {@link Station#id}，标识该决策作用于哪个电站 */
    private Long stationId;

    /** 关联智能场景ID，关联 {@link SmartScene#id}，标识该决策由哪个场景触发 */
    private Long sceneId;

    /**
     * 触发决策的原始数据（JSON格式字符串）。
     * <p>
     * 记录触发本次决策的设备数据快照，如 {"power": 85.5, "temperature": 62.3, "soc": 15}。
     * 保存原始数据便于事后复盘和分析决策是否合理。
     * </p>
     */
    private String triggerData;

    /**
     * AI生成的决策结果（JSON格式字符串）。
     * <p>
     * 包含系统最终决定执行的动作，如 {"action": "stop_discharge", "reason": "SOC过低"}。
     * 决策结果是AI分析和规则匹配的最终输出。
     * </p>
     */
    private String decisionResult;

    /**
     * 决策执行状态。
     * <p>
     * 典型取值：0-待执行、1-执行成功、2-执行失败、3-已取消。
     * 通过状态追踪可以及时发现执行失败的决策并进行补偿处理。
     * </p>
     */
    private Integer status;

    /**
     * 决策阶段编码。
     * <p>
     * 用于支持多阶段决策流程，如：1-数据分析阶段、2-策略生成阶段、3-执行确认阶段。
     * 复杂场景下一次决策可能经历多个阶段，每个阶段产生一条记录。
     * </p>
     */
    private Integer stage;

    /**
     * AI模型的原始响应内容。
     * <p>
     * 记录大语言模型（LLM）返回的完整分析文本，包含推理过程和建议。
     * 保存AI响应有助于评估模型效果和调优提示词（Prompt）。
     * </p>
     */
    private String aiResponse;

    /** 决策实际执行时间，即动作被下发到设备或系统执行操作的时刻 */
    private LocalDateTime executedAt;

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
