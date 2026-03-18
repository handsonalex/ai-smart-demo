package com.smart.infrastructure.kafka;

import com.smart.common.constants.KafkaTopics;
import com.smart.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 设备数据消费者
 *
 * @author Joseph Ho
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceDataConsumer {

    /**
     * 消费设备上报数据
     */
    @KafkaListener(topics = KafkaTopics.DEVICE_DATA, groupId = "smart-group")
    public void consume(String message) {
        log.info("收到设备数据: {}", message);
        DeviceDataMessage data = JsonUtil.fromJson(message, DeviceDataMessage.class);
        if (data == null) {
            log.warn("设备数据反序列化失败: {}", message);
            return;
        }
        // TODO: 调用 DecisionAppService 触发决策流程
        log.info("设备数据处理完成, deviceId: {}, stationId: {}", data.getDeviceId(), data.getStationId());
    }
}
