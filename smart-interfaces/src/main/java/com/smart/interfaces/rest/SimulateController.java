package com.smart.interfaces.rest;

import com.smart.application.service.DecisionAppService;
import com.smart.common.result.Result;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟测试控制器（仅 dev 环境）
 *
 * @author Joseph Ho
 */
@Tag(name = "模拟测试（仅 dev 环境）")
@Profile("dev")
@RestController
@RequestMapping("/api/v1/simulate")
@RequiredArgsConstructor
public class SimulateController {

    private final DecisionAppService decisionAppService;

    @Operation(summary = "模拟设备上报数据并触发决策")
    @PostMapping("/device-data")
    public Result<String> simulateDeviceData(@RequestBody DeviceDataMessage data) {
        decisionAppService.processDeviceData(data);
        return Result.success("模拟数据已提交，决策流程已触发");
    }
}
