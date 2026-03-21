package com.smart.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 序列化/反序列化工具类
 * <p>
 * 基于 Jackson 的 {@link ObjectMapper} 封装，提供统一的 JSON 转换能力。
 * 采用 final 类 + 私有构造器的设计模式，确保该类不可被实例化和继承，仅通过静态方法调用。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>使用单例 ObjectMapper 实例，避免重复创建带来的性能损耗（ObjectMapper 是线程安全的）</li>
 *   <li>在静态初始化块中预配置了容错性设置，增强 JSON 解析的健壮性</li>
 *   <li>所有方法均进行 null 安全检查，转换异常时记录日志并返回 null，避免向上抛出异常</li>
 * </ul>
 * </p>
 *
 * @author Joseph Ho
 */
@Slf4j
public final class JsonUtil {

    /** 私有构造器，防止外部实例化此工具类 */
    private JsonUtil() {
    }

    /** 全局唯一的 Jackson ObjectMapper 实例，线程安全，可在多线程环境下共享使用 */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /* 静态初始化块：配置 ObjectMapper 的全局行为 */
    static {
        // 反序列化时忽略 JSON 中存在但 Java 对象中不存在的属性，避免因字段不匹配而抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时允许空对象（没有任何属性的 Bean），避免序列化空对象时抛出异常
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 将 Java 对象序列化为 JSON 字符串
     *
     * @param obj 待序列化的对象，可以是任意类型（POJO、集合、Map 等）
     * @return JSON 字符串；如果入参为 null 或序列化失败，返回 null
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的 Java 对象
     * <p>
     * 适用于非泛型类型的反序列化，如 fromJson(json, User.class)。
     * </p>
     *
     * @param json  JSON 字符串
     * @param clazz 目标对象的 Class 类型
     * @param <T>   目标类型
     * @return 反序列化后的对象；如果 JSON 为空或反序列化失败，返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的 Java 对象（支持泛型）
     * <p>
     * 适用于带泛型的复杂类型反序列化，如 fromJson(json, new TypeReference&lt;List&lt;User&gt;&gt;(){})。
     * 通过 {@link TypeReference} 在运行时保留泛型类型信息，解决 Java 泛型擦除问题。
     * </p>
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用，用于保留泛型信息
     * @param <T>           目标类型
     * @return 反序列化后的对象；如果 JSON 为空或反序列化失败，返回 null
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }
}
