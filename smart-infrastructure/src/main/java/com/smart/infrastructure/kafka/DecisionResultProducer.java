package com.smart.infrastructure.kafka;

import com.smart.common.constants.KafkaTopics;
import com.smart.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 决策结果生产者
 *
 * @author Joseph Ho
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionResultProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送决策结果
     */
    public void send(DecisionResultMessage message) {
        String json = JsonUtil.toJson(message);
        kafkaTemplate.send(KafkaTopics.DECISION_RESULT, String.valueOf(message.getDecisionId()), json);
        log.info("发送决策结果, decisionId: {}", message.getDecisionId());
    }
}
