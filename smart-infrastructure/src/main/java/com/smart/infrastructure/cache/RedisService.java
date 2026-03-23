package com.smart.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * Redis 通用操作服务接口
 *
 * @author Joseph Ho
 */
public interface RedisService {

    void set(String key, Object value);

    void set(String key, Object value, long timeout, TimeUnit unit);

    <T> T get(String key);

    Boolean delete(String key);

    Boolean hasKey(String key);

    Boolean expire(String key, long timeout, TimeUnit unit);
}
