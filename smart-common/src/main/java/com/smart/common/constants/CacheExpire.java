package com.smart.common.constants;

/**
 * 缓存过期时间常量定义类（单位：秒）
 * <p>
 * 集中管理系统中常用的缓存过期时间，避免在代码中出现魔法数字。
 * 采用 final 类 + 私有构造器的设计模式，确保该类不可被实例化和继承。
 * </p>
 * <p>
 * 使用场景示例：在 Redis 缓存操作中指定 TTL（Time To Live），
 * 如 redisTemplate.opsForValue().set(key, value, CacheExpire.FIVE_MINUTES, TimeUnit.SECONDS)
 * </p>
 *
 * @author Joseph Ho
 */
public final class CacheExpire {

    /** 私有构造器，防止外部实例化此工具类 */
    private CacheExpire() {
    }

    /** 1 分钟过期时间（60 秒），适用于高频变更的临时数据 */
    public static final long ONE_MINUTE = 60L;

    /** 5 分钟过期时间（300 秒），适用于设备状态等需要较高时效性的缓存 */
    public static final long FIVE_MINUTES = 300L;

    /** 10 分钟过期时间（600 秒），适用于场景配置等中等频率变更的缓存 */
    public static final long TEN_MINUTES = 600L;

    /** 1 小时过期时间（3600 秒），适用于相对稳定的业务数据缓存 */
    public static final long ONE_HOUR = 3600L;

    /** 1 天过期时间（86400 秒），适用于基础配置等低频变更的数据缓存 */
    public static final long ONE_DAY = 86400L;
}
