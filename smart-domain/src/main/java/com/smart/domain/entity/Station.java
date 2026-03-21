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
 * 电站实体类，对应MySQL数据库中的 t_station 表。
 * <p>
 * 电站是整个智慧能源系统的顶层管理单元，一个电站下可以包含多个设备（{@link Device}）。
 * 电站记录了光伏/储能站点的基础信息，包括名称、位置、装机容量、运行状态以及联系人等。
 * 系统中的设备数据采集、智能场景联动、AI决策等功能都以电站为维度进行组织和管理。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_station")
public class Station {

    /**
     * 电站唯一标识（主键）。
     * <p>
     * 使用雪花算法（ASSIGN_ID）自动生成分布式唯一ID，避免自增ID在分库分表场景下的冲突问题。
     * </p>
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 电站名称，如"深圳南山光伏电站"，用于界面展示和检索 */
    private String stationName;

    /** 电站地理位置描述，如"广东省深圳市南山区"，用于标识电站所在区域 */
    private String location;

    /** 电站装机容量（单位：kW），表示该电站可承载的最大发电/储能功率 */
    private BigDecimal capacity;

    /**
     * 电站运行状态。
     * <p>
     * 典型取值：0-停运、1-正常运行、2-故障、3-检修中。
     * 通过状态字段可以快速筛选和监控电站的运行情况。
     * </p>
     */
    private Integer status;

    /** 电站联系人姓名，用于运维沟通和告警通知 */
    private String contactPerson;

    /** 电站联系人电话，用于紧急联络和短信告警 */
    private String contactPhone;

    /**
     * 记录创建时间，仅在插入时自动填充。
     * <p>
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略，在新增记录时由框架自动设置当前时间，
     * 无需业务代码手动赋值。需配合 MetaObjectHandler 实现类使用。
     * </p>
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间，在插入和更新时自动填充。
     * <p>
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT_UPDATE} 策略，在新增和修改记录时都会自动更新为当前时间，
     * 方便追踪数据的最近变更时间。
     * </p>
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
