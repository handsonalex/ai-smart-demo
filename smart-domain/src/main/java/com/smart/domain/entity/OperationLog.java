package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类，对应MySQL数据库中的 t_operation_log 表。
 * <p>
 * 该实体用于记录系统中所有重要的用户操作和系统行为，提供完整的操作审计追踪能力。
 * 操作日志涵盖了电站管理、设备控制、场景配置、知识库维护等各类操作，
 * 是系统安全审计和问题排查的重要依据。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_operation_log")
public class OperationLog {

    /**
     * 日志记录唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 操作类型，描述执行了什么操作。
     * <p>
     * 典型取值："CREATE"（新增）、"UPDATE"（修改）、"DELETE"（删除）、"QUERY"（查询）、"EXECUTE"（执行）等。
     * 用于按操作类型进行日志筛选和统计分析。
     * </p>
     */
    private String operationType;

    /** 操作人名称，记录是谁执行了该操作，用于责任追溯 */
    private String operatorName;

    /**
     * 操作目标类型，描述操作作用于什么对象。
     * <p>
     * 典型取值："STATION"（电站）、"DEVICE"（设备）、"SCENE"（场景）、"KNOWLEDGE"（知识库）等。
     * 配合 targetId 可以精确定位被操作的具体对象。
     * </p>
     */
    private String targetType;

    /** 操作目标ID，记录被操作对象的主键ID，与 targetType 配合使用可定位到具体记录 */
    private Long targetId;

    /**
     * 操作详情（JSON格式字符串或自然语言描述）。
     * <p>
     * 记录操作的详细信息，如修改前后的字段值、执行参数、错误信息等，
     * 便于问题排查和操作回溯。
     * </p>
     */
    private String detail;

    /**
     * 记录创建时间（即操作发生时间），仅在插入时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
