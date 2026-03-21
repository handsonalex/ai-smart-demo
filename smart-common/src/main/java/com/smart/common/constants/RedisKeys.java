package com.smart.common.constants;

/**
 * Redis 缓存键前缀常量定义类
 * <p>
 * 集中管理系统中所有 Redis 缓存键的前缀，确保键名规范统一，避免不同模块之间的键冲突。
 * 采用 final 类 + 私有构造器的设计模式，确保该类不可被实例化和继承。
 * </p>
 * <p>
 * 键命名规范：使用冒号分隔的层级结构 "smart:模块:子模块:"，这也是 Redis 的通用命名最佳实践，
 * 便于在 Redis 可视化工具中按层级浏览。
 * </p>
 *
 * @author Joseph Ho
 */
public final class RedisKeys {

    /** 私有构造器，防止外部实例化此工具类 */
    private RedisKeys() {
    }

    /**
     * 场景配置缓存键前缀
     * <p>
     * 完整键格式：smart:scenario:{scenarioId}
     * 缓存内容：场景的完整配置信息（包括关联的规则条件等），
     * 用于决策引擎快速获取场景配置，避免频繁查库。
     * </p>
     */
    public static final String SCENARIO_PREFIX = "smart:scenario:";

    /**
     * 决策记录缓存键前缀
     * <p>
     * 完整键格式：smart:decision:{decisionId}
     * 缓存内容：决策执行状态和结果，
     * 用于支持决策状态的实时查询和防重复决策。
     * </p>
     */
    public static final String DECISION_PREFIX = "smart:decision:";

    /**
     * 设备状态缓存键前缀
     * <p>
     * 完整键格式：smart:device:status:{deviceId}
     * 缓存内容：设备最新上报的状态数据（如 SOC、功率、电压、温度等），
     * 供决策引擎在规则匹配时快速读取设备实时状态。
     * </p>
     */
    public static final String DEVICE_STATUS_PREFIX = "smart:device:status:";

    /**
     * 电站信息缓存键前缀
     * <p>
     * 完整键格式：smart:station:{stationId}
     * 缓存内容：电站基本信息及其关联的设备列表，
     * 用于减少电站信息的数据库查询次数。
     * </p>
     */
    public static final String STATION_PREFIX = "smart:station:";
}
