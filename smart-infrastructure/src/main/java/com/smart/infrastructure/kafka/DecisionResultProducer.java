package com.smart.infrastructure.kafka;

import com.smart.common.constants.KafkaTopics;
import com.smart.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 决策结果 Kafka 生产者
 *
 * <p>本类负责将智能决策引擎产生的决策结果发送到 Kafka，供下游系统（如设备控制系统、
 * 监控告警系统等）消费和执行。在整个数据流转架构中的位置：</p>
 * <pre>
 * 决策引擎 -> DecisionResultProducer（本类） -> Kafka(decision-result topic) -> 下游执行系统
 * </pre>
 *
 * <p>消息发送策略：使用 decisionId 作为 Kafka 消息的 key，确保同一决策的消息
 * 会被路由到同一个分区（partition），保证同一决策相关消息的顺序性。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionResultProducer {

    /** Kafka 消息发送模板，由 Spring Boot 自动配置并注入 */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送决策结果到 Kafka
     *
     * <p>将决策结果序列化为 JSON 后发送到 decision-result topic。
     * 使用 decisionId 作为消息 key，保证同一决策的消息落入同一分区。</p>
     *
     * @param message 决策结果消息体
     */
    public void send(DecisionResultMessage message) {
        // 将决策结果对象序列化为 JSON 字符串
        String json = JsonUtil.toJson(message);
        // 发送到 Kafka，key 为 decisionId（用于分区路由），value 为 JSON 消息体
        kafkaTemplate.send(KafkaTopics.DECISION_RESULT, String.valueOf(message.getDecisionId()), json);
        log.info("发送决策结果, decisionId: {}", message.getDecisionId());
    }
}
