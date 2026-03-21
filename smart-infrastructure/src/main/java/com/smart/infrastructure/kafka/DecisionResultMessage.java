package com.smart.infrastructure.kafka;

import lombok.Data;
import java.io.Serializable;

/**
 * 决策结果消息体 DTO（Data Transfer Object）
 *
 * <p>用于 Kafka 消息传输的决策结果数据结构。当智能决策引擎完成决策后，
 * 将决策结果封装为此对象，通过 {@link DecisionResultProducer} 发送到 Kafka，
 * 供下游执行系统消费。</p>
 *
 * <p>实现 {@link Serializable} 接口以支持序列化传输。</p>
 *
 * @author Joseph Ho
 */
@Data
public class DecisionResultMessage implements Serializable {

    /** 决策记录唯一标识 */
    private Long decisionId;

    /** 电站 ID，标识决策所属的电站 */
    private Long stationId;

    /** 场景 ID，标识触发决策的场景 */
    private Long sceneId;

    /** 决策结果内容（如具体的调控指令、推荐方案等） */
    private String decisionResult;

    /** 决策状态（如：0-待执行，1-执行中，2-已完成，3-执行失败） */
    private Integer status;

    /** AI 模型的原始响应内容（大模型返回的完整推理文本） */
    private String aiResponse;
}
