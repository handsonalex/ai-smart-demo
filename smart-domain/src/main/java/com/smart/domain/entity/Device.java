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
 * 设备实体类，对应MySQL数据库中的 t_device 表。
 * <p>
 * 设备是电站（{@link Station}）下的核心物联网资产，如光伏逆变器、储能电池、电表等。
 * 每个设备归属于某个电站，通过 stationId 与电站形成多对一关系。
 * 设备会周期性上报运行数据（{@link DeviceData}），系统据此进行实时监控和AI决策。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_device")
public class Device {

    /**
     * 设备唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属电站ID，关联 {@link Station#id}，表示该设备安装在哪个电站 */
    private Long stationId;

    /** 设备名称，如"1号逆变器"、"A组储能电池"，用于界面展示和运维识别 */
    private String deviceName;

    /**
     * 设备类型编码。
     * <p>
     * 典型取值：1-光伏逆变器、2-储能电池、3-电表、4-环境监测仪等。
     * 不同类型的设备上报的数据指标有所不同，AI决策也会根据设备类型采取不同策略。
     * </p>
     */
    private Integer deviceType;

    /**
     * 设备序列号（Serial Number），设备出厂时的唯一编号。
     * <p>
     * 用于设备身份识别和物联网通信时的设备认证，在系统中应保持唯一。
     * </p>
     */
    private String deviceSn;

    /** 设备额定功率（单位：kW），表示设备在标准工况下的最大输出/输入功率 */
    private BigDecimal ratedPower;

    /**
     * 设备运行状态。
     * <p>
     * 典型取值：0-离线、1-在线运行、2-故障、3-维护中。
     * 设备状态是实时监控和告警的重要依据。
     * </p>
     */
    private Integer status;

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
