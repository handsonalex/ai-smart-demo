package com.smart.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Kafka 配置类
 *
 * <p>本项目使用 Kafka 作为消息中间件，实现设备数据采集与决策结果下发的异步解耦。
 * 消息流转架构如下：</p>
 * <ul>
 *   <li>设备数据上报：IoT 网关 -> Kafka(device-data topic) -> {@code DeviceDataConsumer} 消费并触发决策</li>
 *   <li>决策结果下发：决策引擎 -> {@code DecisionResultProducer} -> Kafka(decision-result topic) -> 下游执行系统</li>
 * </ul>
 *
 * <p>当前 Kafka 的 Consumer 和 Producer 配置（如 bootstrap-servers、group-id、序列化器等）
 * 全部通过 application.yml 中的 spring.kafka.* 属性由 Spring Boot 自动配置完成，
 * 因此本类暂无需额外的 Bean 定义。</p>
 *
 * <p>如果后续需要自定义消费者并发数、重试策略、死信队列等高级特性，可在本类中添加
 * ConcurrentKafkaListenerContainerFactory、KafkaTemplate 等自定义 Bean。</p>
 *
 * @author Joseph Ho
 */
@Configuration
public class KafkaConfig {
}
