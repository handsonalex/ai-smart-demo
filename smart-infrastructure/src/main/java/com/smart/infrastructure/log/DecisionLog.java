package com.smart.infrastructure.log;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 决策日志（存储到 Elasticsearch）
 *
 * @author Joseph Ho
 */
@Data
public class DecisionLog {
    private String id;
    private Long decisionId;
    private Long stationId;
    private Long sceneId;
    private String stage;
    private String input;
    private String output;
    private Long costMs;
    private Boolean success;
    private String errorMsg;
    private LocalDateTime createTime;
}
