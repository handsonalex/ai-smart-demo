package com.smart.infrastructure;

import com.smart.common.utils.JsonUtil;
import com.smart.infrastructure.kafka.DeviceDataConsumer;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeviceDataConsumerTest {

    @InjectMocks
    private DeviceDataConsumer deviceDataConsumer;

    @Test
    @DisplayName("正常消息应成功消费")
    void consumeValidMessage() {
        DeviceDataMessage msg = new DeviceDataMessage();
        msg.setDeviceId(1L);
        msg.setStationId(1L);
        msg.setPower(new BigDecimal("50"));
        msg.setSoc(new BigDecimal("70"));
        String json = JsonUtil.toJson(msg);

        assertDoesNotThrow(() -> deviceDataConsumer.consume(json));
    }

    @Test
    @DisplayName("空消息不应抛出异常")
    void consumeInvalidMessage() {
        assertDoesNotThrow(() -> deviceDataConsumer.consume("invalid json"));
    }

    @Test
    @DisplayName("null 消息体反序列化后应打印警告")
    void consumeNullBody() {
        assertDoesNotThrow(() -> deviceDataConsumer.consume("null"));
    }
}
