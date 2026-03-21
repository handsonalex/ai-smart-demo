package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建电站请求 DTO
 *
 * <p>用于接收前端创建电站时提交的参数。通过 JSR 380 注解定义参数校验规则，
 * 校验失败时由 {@link com.smart.interfaces.handler.GlobalExceptionHandler} 统一处理。
 *
 * <p>字段校验策略：
 * <ul>
 *   <li>必填字段使用 {@code @NotBlank} 或 {@code @NotNull} 标注</li>
 *   <li>选填字段允许为 null，由业务层决定默认值</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "创建电站请求")
public class StationCreateReq {
    /** 电站名称，必填，用于唯一标识一个电站（业务上不允许为空） */
    @NotBlank(message = "电站名称不能为空")
    @Schema(description = "电站名称")
    private String stationName;

    /** 电站地理位置，选填，描述电站的物理地址或经纬度 */
    @Schema(description = "电站位置")
    private String location;

    /** 装机容量（单位：kW），必填，表示电站的总发电/储能能力 */
    @NotNull(message = "装机容量不能为空")
    @Schema(description = "装机容量(kW)")
    private BigDecimal capacity;

    /** 联系人姓名，选填，电站的运维负责人 */
    @Schema(description = "联系人")
    private String contactPerson;

    /** 联系电话，选填，运维联系电话 */
    @Schema(description = "联系电话")
    private String contactPhone;
}
