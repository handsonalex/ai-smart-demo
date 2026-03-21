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
 * 设备上报数据实体类，对应MySQL数据库中的 t_device_data 表。
 * <p>
 * 该实体记录了物联网设备周期性采集并上报的运行数据，是系统进行实时监控、规则匹配和AI决策的核心数据来源。
 * 每条记录代表某台设备在某个时刻的一次数据快照，包含功率、电压、电流、温度、SOC等关键指标。
 * 系统通过将设备数据与场景规则（{@link SceneRule}）进行匹配，判断是否触发自动化决策。
 * </p>
 * <p>
 * 注意：该表数据量增长较快（每台设备每分钟可能产生一条记录），在生产环境中需要考虑分表或归档策略。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_device_data")
public class DeviceData {

    /**
     * 数据记录唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 上报设备ID，关联 {@link Device#id}，标识数据来源设备 */
    private Long deviceId;

    /** 所属电站ID，关联 {@link Station#id}，冗余存储以便按电站维度快速查询，避免多表联查 */
    private Long stationId;

    /** 实时功率（单位：kW），表示设备当前的输出/输入功率 */
    private BigDecimal power;

    /** 实时电压（单位：V），表示设备当前的工作电压 */
    private BigDecimal voltage;

    /** 实时电流（单位：A），表示设备当前的工作电流 */
    private BigDecimal current;

    /** 实时温度（单位：℃），表示设备或环境的当前温度，用于过温告警等场景 */
    private BigDecimal temperature;

    /**
     * 电池荷电状态（State of Charge，单位：%），取值范围 0~100。
     * <p>
     * 仅对储能类设备有意义，表示电池剩余电量百分比。
     * SOC是储能调度决策的关键指标，如低SOC时停止放电、高SOC时停止充电。
     * </p>
     */
    private BigDecimal soc;

    /** 数据采集时间，即设备端实际采集该组数据的时刻（可能与服务端接收时间存在延迟） */
    private LocalDateTime collectTime;

    /**
     * 记录创建时间（即服务端入库时间），仅在插入时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
