package com.smart.infrastructure.kafka;

import lombok.Data;
import java.io.Serializable;

/**
 * 决策结果消息体
 *
 * @author Joseph Ho
 */
@Data
public class DecisionResultMessage implements Serializable {
    private Long decisionId;
    private Long stationId;
    private Long sceneId;
    private String decisionResult;
    private Integer status;
    private String aiResponse;
}
