package com.smart.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用操作服务
 *
 * <p>对 {@link RedisTemplate} 进行轻量级封装，提供简洁的 API 供上层业务服务调用。
 * 封装的目的是：</p>
 * <ul>
 *   <li>统一 Redis 操作入口，避免业务代码直接依赖 RedisTemplate 的复杂 API</li>
 *   <li>方便后续扩展公共逻辑（如统一的 key 前缀、异常处理、监控埋点等）</li>
 *   <li>便于单元测试时进行 Mock</li>
 * </ul>
 *
 * <p>本类提供了 Redis String 类型的基本操作：设置、获取、删除、判断存在、设置过期时间。
 * 如需 Hash、List、Set、ZSet 等数据结构操作，可在此类中扩展。</p>
 *
 * @author Joseph Ho
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    /** 已配置好序列化策略的 RedisTemplate（参见 {@link com.smart.infrastructure.config.RedisConfig}） */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置 key-value（无过期时间）
     *
     * @param key   Redis key
     * @param value 值（会被 JSON 序列化后存储）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置 key-value 并指定过期时间
     *
     * @param key     Redis key
     * @param value   值（会被 JSON 序列化后存储）
     * @param timeout 过期时间数值
     * @param unit    过期时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取 key 对应的值
     *
     * <p>利用泛型自动转换返回类型，调用方无需手动强转。
     * 注意：如果存储的对象类型与期望的泛型类型不匹配，可能抛出 ClassCastException。</p>
     *
     * @param key Redis key
     * @param <T> 期望的返回值类型
     * @return 反序列化后的值，如果 key 不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定 key
     *
     * @param key Redis key
     * @return 删除成功返回 true，key 不存在返回 false
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key Redis key
     * @return 存在返回 true，不存在返回 false
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 为已有的 key 设置过期时间
     *
     * @param key     Redis key
     * @param timeout 过期时间数值
     * @param unit    过期时间单位
     * @return 设置成功返回 true，key 不存在返回 false
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}
