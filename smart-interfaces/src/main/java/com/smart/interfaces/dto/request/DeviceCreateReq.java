package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建设备请求
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建设备请求")
public class DeviceCreateReq {
    @NotNull(message = "电站ID不能为空")
    @Schema(description = "电站ID")
    private Long stationId;

    @NotBlank(message = "设备名称不能为空")
    @Schema(description = "设备名称")
    private String deviceName;

    @NotNull(message = "设备类型不能为空")
    @Schema(description = "设备类型: 1-光伏板 2-储能电池 3-逆变器 4-电表")
    private Integer deviceType;

    @Schema(description = "设备序列号")
    private String deviceSn;

    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;
}
