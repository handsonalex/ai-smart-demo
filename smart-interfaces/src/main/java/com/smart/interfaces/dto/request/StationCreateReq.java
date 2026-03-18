package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建电站请求
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建电站请求")
public class StationCreateReq {
    @NotBlank(message = "电站名称不能为空")
    @Schema(description = "电站名称")
    private String stationName;

    @Schema(description = "电站位置")
    private String location;

    @NotNull(message = "装机容量不能为空")
    @Schema(description = "装机容量(kW)")
    private BigDecimal capacity;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;
}
