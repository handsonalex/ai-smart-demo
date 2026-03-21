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
 * 模拟测试控制器（仅 dev 环境生效）—— 接口层（Interfaces Layer）
 *
 * <p>提供开发和测试环境下的数据模拟接口，用于在没有真实设备的情况下
 * 模拟设备数据上报，触发完整的决策流程。
 * <ul>
 *   <li>POST /api/v1/simulate/device-data — 模拟设备上报数据并触发决策</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>{@code @Profile("dev")} 注解确保此 Controller 仅在 dev 环境下被 Spring 容器加载，
 *       生产环境（prod）中该 Bean 不会被注册，从而避免测试接口暴露到生产环境</li>
 *   <li>模拟接口直接调用 {@link DecisionAppService#processDeviceData}，
 *       绕过 Kafka 消息队列，同步触发决策流程，便于开发调试</li>
 *   <li>在正式环境中，设备数据通过 Kafka 消费者异步接收并处理</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "模拟测试（仅 dev 环境）")
@Profile("dev")
@RestController
@RequestMapping("/api/v1/simulate")
@RequiredArgsConstructor
public class SimulateController {

    /** 决策应用服务，用于直接触发决策流程（绕过 Kafka） */
    private final DecisionAppService decisionAppService;

    /**
     * 模拟设备上报数据并触发决策
     *
     * <p>处理流程：接收模拟的设备数据消息 → 直接调用决策服务处理 → 返回成功提示
     * <p>该接口模拟了 Kafka 消费者收到设备数据后的处理逻辑，
     * 开发者可以通过 Swagger/Postman 手动构造设备数据进行端到端测试
     *
     * @param data 模拟的设备数据消息，格式与 Kafka 消息体一致
     * @return 成功提示信息
     */
    @Operation(summary = "模拟设备上报数据并触发决策")
    @PostMapping("/device-data")
    public Result<String> simulateDeviceData(@RequestBody DeviceDataMessage data) {
        decisionAppService.processDeviceData(data);
        return Result.success("模拟数据已提交，决策流程已触发");
    }
}
