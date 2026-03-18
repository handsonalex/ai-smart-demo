package com.smart.interfaces.dto.cache;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 场景缓存 DTO
 *
 * @author Joseph Ho
 */
@Data
public class SmartSceneDTO implements Serializable {
    private Long id;
    private String sceneName;
    private Integer scenarioType;
    private Long stationId;
    private Boolean enabled;
    private Integer priority;
    private String description;
    private List<RuleDTO> rules;

    @Data
    public static class RuleDTO implements Serializable {
        private Long id;
        private Integer conditionType;
        private Integer conditionSign;
        private String thresholdValue;
        private String action;
    }
}
