package com.smart.interfaces.dto.cache;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 场景缓存 DTO
 *
 * <p>用于 Redis 缓存中存储场景及其规则的完整数据。实现 {@link Serializable} 接口，
 * 支持 Java 序列化存入 Redis（也可配合 Jackson 等 JSON 序列化方案使用）。
 *
 * <p>设计意图：
 * <ul>
 *   <li>决策引擎在处理设备数据时需要频繁查询场景和规则信息</li>
 *   <li>为避免每次决策都查询数据库，将场景数据缓存到 Redis 中</li>
 *   <li>该 DTO 是缓存层专用的数据传输对象，与领域实体和接口 DTO 解耦</li>
 *   <li>缓存的场景数据包含完整的规则列表，决策引擎可一次性获取所有需要的信息</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
public class SmartSceneDTO implements Serializable {
    /** 场景主键 ID */
    private Long id;

    /** 场景名称 */
    private String sceneName;

    /** 场景类型：1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电 */
    private Integer scenarioType;

    /** 所属电站 ID */
    private Long stationId;

    /** 是否启用，缓存中也需要保留此标志以便决策引擎快速判断 */
    private Boolean enabled;

    /** 优先级，决策引擎按优先级排序匹配场景 */
    private Integer priority;

    /** 场景描述 */
    private String description;

    /** 关联的规则列表，缓存中包含完整规则以减少数据库查询 */
    private List<RuleDTO> rules;

    /**
     * 规则缓存 DTO
     *
     * <p>场景规则的缓存数据结构，包含条件定义和动作定义。
     * 同样实现 {@link Serializable} 接口以支持序列化。
     */
    @Data
    public static class RuleDTO implements Serializable {
        /** 规则主键 ID */
        private Long id;

        /** 条件类型：1-SOC 2-功率 3-电压 4-温度 5-时间段 */
        private Integer conditionType;

        /** 比较运算符：1-> 2->= 3-< 4-<= 5-== 6-between */
        private Integer conditionSign;

        /** 阈值（字符串类型，支持单值和区间格式） */
        private String thresholdValue;

        /** 动作指令 */
        private String action;
    }
}
