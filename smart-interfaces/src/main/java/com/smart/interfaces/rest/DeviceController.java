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
 * 设备管理控制器
 *
 * @author Joseph Ho
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceAppService deviceAppService;

    @Operation(summary = "创建设备")
    @PostMapping
    public Result<DeviceResp> create(@Valid @RequestBody DeviceCreateReq req) {
        Device device = new Device();
        BeanUtils.copyProperties(req, device);
        Device created = deviceAppService.create(device);
        return Result.success(toResp(created));
    }

    @Operation(summary = "查询设备详情")
    @GetMapping("/{id}")
    public Result<DeviceResp> getById(@PathVariable Long id) {
        Device device = deviceAppService.getById(id);
        return Result.success(toResp(device));
    }

    @Operation(summary = "查询电站下设备列表")
    @GetMapping("/station/{stationId}")
    public Result<List<DeviceResp>> listByStation(@PathVariable Long stationId) {
        List<Device> devices = deviceAppService.listByStationId(stationId);
        return Result.success(devices.stream().map(this::toResp).toList());
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceAppService.delete(id);
        return Result.success();
    }

    private DeviceResp toResp(Device device) {
        DeviceResp resp = new DeviceResp();
        BeanUtils.copyProperties(device, resp);
        return resp;
    }
}
