package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景类型枚举
 * <p>
 * 定义智慧能源系统中支持的业务场景类型。场景是决策引擎的核心概念，
 * 不同的场景类型对应不同的能源管理策略和决策规则。
 * </p>
 * <p>
 * 设计说明：系统通过场景类型来路由不同的决策逻辑，
 * 每种场景下关联一组特定的规则条件和决策动作。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum ScenarioType {

    /**
     * 削峰填谷 —— 在电价低谷期储能充电，高峰期放电供能，降低用电成本
     */
    PEAK_SHAVING(1, "削峰填谷"),

    /**
     * 需量控制 —— 监控并控制用电需量（最大功率），避免超过电力公司核定容量而产生罚款
     */
    DEMAND_CONTROL(2, "需量控制"),

    /**
     * 光伏自消纳 —— 优先将光伏发电量就地消纳，减少余电上网，提高自用率
     */
    SELF_CONSUMPTION(3, "光伏自消纳"),

    /**
     * 应急备电 —— 在市电停电时，由储能电池自动切换供电，保障关键负载持续运行
     */
    EMERGENCY_BACKUP(4, "应急备电");

    /** 场景类型编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 场景类型的中文描述，用于前端展示和日志输出 */
    private final String description;
}
