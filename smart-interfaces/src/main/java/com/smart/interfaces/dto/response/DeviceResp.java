package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备响应 DTO
 *
 * <p>用于向前端返回设备信息。设备隶属于电站，通过 stationId 关联。
 * 该 DTO 从领域实体 {@code Device} 转换而来。
 *
 * <p>设备类型枚举：
 * <ul>
 *   <li>1 - 光伏板</li>
 *   <li>2 - 储能电池</li>
 *   <li>3 - 逆变器</li>
 *   <li>4 - 电表</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "设备响应")
public class DeviceResp {
    /** 设备主键 ID */
    @Schema(description = "设备ID")
    private Long id;

    /** 所属电站 ID，标识该设备归属哪个电站 */
    @Schema(description = "电站ID")
    private Long stationId;

    /** 设备名称 */
    @Schema(description = "设备名称")
    private String deviceName;

    /** 设备类型：1-光伏板 2-储能电池 3-逆变器 4-电表 */
    @Schema(description = "设备类型")
    private Integer deviceType;

    /** 设备序列号，设备的唯一出厂编号 */
    @Schema(description = "设备序列号")
    private String deviceSn;

    /** 额定功率（单位：kW） */
    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;

    /** 设备状态（具体枚举值由业务定义） */
    @Schema(description = "状态")
    private Integer status;

    /** 创建时间，由数据库自动生成 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
