package com.smart.infrastructure.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 决策日志注解 —— 标记需要自动记录决策日志的方法
 *
 * <p>将此注解添加到决策流程中的关键方法上，{@link DecisionLogAspect} 会自动拦截这些方法，
 * 记录执行耗时、成功状态和异常信息，并持久化到 Elasticsearch。</p>
 *
 * <p>注解属性：</p>
 * <ul>
 *   <li>{@code stage}：决策阶段标识，用于区分同一次决策中的不同处理环节
 *       （如 "rule_match"、"ai_inference"、"result_output"）</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code @LogDecision(stage = "ai_inference")}
 * public AiResult invokeAiModel(String prompt) {
 *     // AI 推理逻辑...
 * }
 * </pre>
 *
 * <p>注解设计说明：</p>
 * <ul>
 *   <li>@Target(METHOD)：只能标注在方法上</li>
 *   <li>@Retention(RUNTIME)：运行时保留，AOP 切面在运行时通过反射读取此注解</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDecision {
    /**
     * 决策阶段标识
     *
     * <p>用于标识当前方法属于决策流程中的哪个阶段，
     * 该值会被记录到 {@link DecisionLog#getStage()} 字段中。</p>
     *
     * @return 阶段标识字符串，默认为空
     */
    String stage() default "";
}
