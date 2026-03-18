package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备响应
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "设备响应")
public class DeviceResp {
    @Schema(description = "设备ID")
    private Long id;
    @Schema(description = "电站ID")
    private Long stationId;
    @Schema(description = "设备名称")
    private String deviceName;
    @Schema(description = "设备类型")
    private Integer deviceType;
    @Schema(description = "设备序列号")
    private String deviceSn;
    @Schema(description = "额定功率(kW)")
    private BigDecimal ratedPower;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
