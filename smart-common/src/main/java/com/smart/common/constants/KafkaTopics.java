package com.smart.common.constants;

/**
 * Kafka 主题常量定义类
 * <p>
 * 集中管理系统中所有 Kafka 消息主题的名称，避免在代码中硬编码主题字符串。
 * 采用 final 类 + 私有构造器的设计模式，确保该类不可被实例化和继承，仅作为常量容器使用。
 * </p>
 * <p>
 * 主题命名规范：统一使用 "smart-" 前缀，便于在 Kafka 集群中识别本系统的主题。
 * </p>
 *
 * @author Joseph Ho
 */
public final class KafkaTopics {

    /** 私有构造器，防止外部实例化此工具类 */
    private KafkaTopics() {
    }

    /**
     * 设备数据上报主题
     * <p>
     * 用于接收光伏板、储能电池、逆变器、电表等设备通过物联网网关上报的实时数据。
     * 消费者通常是决策引擎服务，用于触发实时决策流程。
     * </p>
     */
    public static final String DEVICE_DATA = "smart-device-data";

    /**
     * 决策结果主题
     * <p>
     * 决策引擎生成决策指令后，将结果发布到此主题。
     * 消费者通常是指令下发服务，负责将决策指令下发到具体设备。
     * </p>
     */
    public static final String DECISION_RESULT = "smart-decision-result";

    /**
     * 决策日志主题
     * <p>
     * 记录决策过程中每个阶段的详细日志（如规则匹配、RAG 检索、AI 推理等），
     * 用于决策链路追踪和问题排查。消费者通常是日志存储服务。
     * </p>
     */
    public static final String DECISION_LOG = "smart-decision-log";
}
