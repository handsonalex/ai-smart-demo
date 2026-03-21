package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 电站响应 DTO
 *
 * <p>用于向前端返回电站信息。该 DTO 从领域实体 {@code Station} 转换而来，
 * 只暴露前端需要的字段，隔离领域模型与接口层的耦合。
 *
 * <p>电站状态枚举：
 * <ul>
 *   <li>0 - 离线：电站通信中断或未启用</li>
 *   <li>1 - 在线：电站正常运行</li>
 *   <li>2 - 告警：电站存在异常（如设备故障、通信不稳定等）</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "电站响应")
public class StationResp {
    /** 电站主键 ID */
    @Schema(description = "电站ID")
    private Long id;

    /** 电站名称 */
    @Schema(description = "电站名称")
    private String stationName;

    /** 电站地理位置 */
    @Schema(description = "位置")
    private String location;

    /** 装机容量（单位：kW） */
    @Schema(description = "装机容量(kW)")
    private BigDecimal capacity;

    /** 电站状态：0-离线 1-在线 2-告警 */
    @Schema(description = "状态: 0-离线 1-在线 2-告警")
    private Integer status;

    /** 联系人姓名 */
    @Schema(description = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 创建时间，由数据库自动生成 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
