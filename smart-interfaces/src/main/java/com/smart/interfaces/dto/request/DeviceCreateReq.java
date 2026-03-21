package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建设备请求 DTO
 *
 * <p>用于接收前端创建设备时提交的参数。设备必须隶属于某个电站（通过 stationId 关联），
 * 体现了电站与设备的聚合关系。
 *
 * <p>设备类型枚举：
 * <ul>
 *   <li>1 - 光伏板：太阳能发电设备</li>
 *   <li>2 - 储能电池：电能存储设备（如锂电池组）</li>
 *   <li>3 - 逆变器：将直流电转换为交流电的设备</li>
 *   <li>4 - 电表：电能计量设备</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建设备请求")
public class DeviceCreateReq {
    /** 所属电站 ID，必填，建立设备与电站的关联关系 */
    @NotNull(message = "电站ID不能为空")
    @Schema(description = "电站ID")
    private Long stationId;

    /** 设备名称，必填，用于标识设备（如"1号光伏板"、"A区储能电池"） */
    @NotBlank(message = "设备名称不能为空")
    @Schema(description = "设备名称")
    private String deviceName;

    /** 设备类型，必填，取值范围：1-光伏板 2-储能电池 3-逆变器 4-电表 */
    @NotNull(message = "设备类型不能为空")
    @Schema(description = "设备类型: 1-光伏板 2-储能电池 3-逆变器 4-电表")
    private Integer deviceType;

    /** 设备序列号，选填，设备的唯一出厂编号，用于设备追溯 */
    @Schema(description = "设备序列号")
    private String deviceSn;

    /** 额定功率（单位：kW），选填，设备的标称功率 */
    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;
}
