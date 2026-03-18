package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "电站响应")
public class StationResp {
    @Schema(description = "电站ID")
    private Long id;
    @Schema(description = "电站名称")
    private String stationName;
    @Schema(description = "位置")
    private String location;
    @Schema(description = "装机容量(kW)")
    private BigDecimal capacity;
    @Schema(description = "状态: 0-离线 1-在线 2-告警")
    private Integer status;
    @Schema(description = "联系人")
    private String contactPerson;
    @Schema(description = "联系电话")
    private String contactPhone;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
