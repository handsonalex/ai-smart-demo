package com.smart.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 *
 * <p>本类自定义 RedisTemplate 的序列化策略，解决 Spring Boot 默认 RedisTemplate 使用 JDK 序列化的问题。
 * JDK 序列化会导致 Redis 中存储的 key 和 value 包含不可读的字节码前缀，不利于调试和运维。</p>
 *
 * <p>序列化策略设计：</p>
 * <ul>
 *   <li>Key / HashKey 使用 {@link StringRedisSerializer}：保证 key 在 Redis 中是可读的纯字符串，
 *       便于通过 Redis CLI 或管理工具直接查看和操作</li>
 *   <li>Value / HashValue 使用 {@link GenericJackson2JsonRedisSerializer}：
 *       将 Java 对象序列化为 JSON 字符串存储，具备以下优势：
 *       <ol>
 *         <li>可读性好，便于排查问题</li>
 *         <li>JSON 中会自动携带 @class 类型信息，反序列化时可还原为原始 Java 类型</li>
 *         <li>跨语言兼容，其他服务可直接读取 JSON 格式数据</li>
 *       </ol>
 *   </li>
 * </ul>
 *
 * <p>@EnableCaching 开启 Spring Cache 抽象层，允许使用 @Cacheable、@CacheEvict 等注解进行声明式缓存管理。</p>
 *
 * @author Joseph Ho
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 自定义 RedisTemplate Bean
     *
     * <p>替代 Spring Boot 自动配置的默认 RedisTemplate（默认使用 JDK 序列化），
     * 使用 String + JSON 的序列化组合方案。</p>
     *
     * @param redisConnectionFactory Redis 连接工厂（由 Spring Boot 自动配置，
     *                                根据 application.yml 中的 spring.redis.* 属性创建）
     * @return 配置好序列化策略的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key 序列化器：将 key 序列化为纯字符串，确保在 Redis 中可读
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // Value 序列化器：将 value 序列化为 JSON，并自动附带类型信息（@class 字段）以支持反序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 普通 key-value 的序列化配置
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        // Hash 结构的序列化配置（与普通 key-value 保持一致的策略）
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 初始化 RedisTemplate，确保所有属性设置生效
        template.afterPropertiesSet();
        return template;
    }
}
