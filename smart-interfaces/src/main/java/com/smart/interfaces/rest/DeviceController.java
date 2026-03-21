package com.smart.interfaces.rest;

import com.smart.application.service.DeviceAppService;
import com.smart.common.result.Result;
import com.smart.domain.entity.Device;
import com.smart.interfaces.dto.request.DeviceCreateReq;
import com.smart.interfaces.dto.response.DeviceResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制器 —— 接口层（Interfaces Layer）
 *
 * <p>提供设备资源的 RESTful 接口，设备隶属于电站（多对一关系）：
 * <ul>
 *   <li>POST   /api/v1/devices                    — 创建设备</li>
 *   <li>GET    /api/v1/devices/{id}               — 查询单个设备详情</li>
 *   <li>GET    /api/v1/devices/station/{stationId} — 查询某电站下的所有设备</li>
 *   <li>DELETE /api/v1/devices/{id}               — 删除设备</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>设备与电站是多对一的聚合关系，通过 stationId 关联</li>
 *   <li>提供按电站维度查询设备列表的接口，满足"查看某电站下所有设备"的业务场景</li>
 *   <li>Controller 仅做协议适配和 DTO 转换，业务逻辑委托给应用服务层</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    /** 设备应用服务，负责编排设备相关的业务逻辑 */
    private final DeviceAppService deviceAppService;

    /**
     * 创建设备
     *
     * <p>处理流程：参数校验 → DTO 转领域对象 → 调用应用服务创建 → 领域对象转响应 DTO
     *
     * @param req 创建设备请求体，包含所属电站ID、设备名称、设备类型等，由 {@code @Valid} 触发校验
     * @return 创建成功的设备信息
     */
    @Operation(summary = "创建设备")
    @PostMapping
    public Result<DeviceResp> create(@Valid @RequestBody DeviceCreateReq req) {
        // 第一步：将请求 DTO 转换为领域实体
        Device device = new Device();
        BeanUtils.copyProperties(req, device);
        // 第二步：调用应用服务执行创建逻辑（内部会校验电站是否存在等业务规则）
        Device created = deviceAppService.create(device);
        // 第三步：将领域实体转换为响应 DTO 返回
        return Result.success(toResp(created));
    }

    /**
     * 查询设备详情
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务查询 → 领域对象转响应 DTO
     *
     * @param id 设备主键 ID
     * @return 设备详情信息
     */
    @Operation(summary = "查询设备详情")
    @GetMapping("/{id}")
    public Result<DeviceResp> getById(@PathVariable Long id) {
        Device device = deviceAppService.getById(id);
        return Result.success(toResp(device));
    }

    /**
     * 查询电站下的设备列表
     *
     * <p>处理流程：路径参数获取电站 ID → 调用应用服务按电站查询 → 批量转换为响应 DTO
     * <p>该接口体现了设备与电站的聚合关系，支持按电站维度查看其下所有设备
     *
     * @param stationId 电站 ID
     * @return 该电站下的所有设备列表
     */
    @Operation(summary = "查询电站下设备列表")
    @GetMapping("/station/{stationId}")
    public Result<List<DeviceResp>> listByStation(@PathVariable Long stationId) {
        List<Device> devices = deviceAppService.listByStationId(stationId);
        return Result.success(devices.stream().map(this::toResp).toList());
    }

    /**
     * 删除设备
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务执行删除
     *
     * @param id 要删除的设备 ID
     * @return 空响应，表示删除成功
     */
    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceAppService.delete(id);
        return Result.success();
    }

    /**
     * 领域实体转响应 DTO 的私有方法
     *
     * <p>将 {@link Device} 领域对象转换为 {@link DeviceResp} 响应对象，
     * 隔离领域模型与接口层的耦合
     *
     * @param device 设备领域实体
     * @return 设备响应 DTO
     */
    private DeviceResp toResp(Device device) {
        DeviceResp resp = new DeviceResp();
        BeanUtils.copyProperties(device, resp);
        return resp;
    }
}
