package com.smart.infrastructure.kafka;

import com.smart.common.constants.KafkaTopics;
import com.smart.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 设备数据 Kafka 消费者
 *
 * <p>本类是智能决策系统的数据入口，负责从 Kafka 消费 IoT 设备上报的实时数据。
 * 在整个数据流转架构中的位置：</p>
 * <pre>
 * IoT 设备 -> 网关 -> Kafka(device-data topic) -> DeviceDataConsumer（本类） -> 决策引擎 -> 决策结果
 * </pre>
 *
 * <p>消费流程：</p>
 * <ol>
 *   <li>从 Kafka 的 device-data topic 接收 JSON 格式的设备数据消息</li>
 *   <li>将 JSON 反序列化为 {@link DeviceDataMessage} 对象</li>
 *   <li>调用决策应用服务触发智能决策流程（待实现）</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceDataConsumer {

    /**
     * 消费设备上报数据
     *
     * <p>@KafkaListener 声明式地将本方法注册为 Kafka 消费者：</p>
     * <ul>
     *   <li>topics：监听的 topic 名称，由 {@link KafkaTopics#DEVICE_DATA} 常量统一管理</li>
     *   <li>groupId：消费者组 ID，同一组内的消费者实例会分摊 topic 的分区，实现负载均衡</li>
     * </ul>
     *
     * @param message Kafka 消息体（JSON 格式的设备数据字符串）
     */
    @KafkaListener(topics = KafkaTopics.DEVICE_DATA, groupId = "smart-group")
    public void consume(String message) {
        log.info("收到设备数据: {}", message);
        // 将 JSON 字符串反序列化为设备数据消息对象
        DeviceDataMessage data = JsonUtil.fromJson(message, DeviceDataMessage.class);
        if (data == null) {
            log.warn("设备数据反序列化失败: {}", message);
            return;
        }
        // TODO: 调用 DecisionAppService 触发决策流程
        log.info("设备数据处理完成, deviceId: {}, stationId: {}", data.getDeviceId(), data.getStationId());
    }
}
