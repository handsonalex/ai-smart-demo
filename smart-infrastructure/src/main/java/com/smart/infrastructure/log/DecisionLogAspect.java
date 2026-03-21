package com.smart.infrastructure.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 决策日志 AOP 切面 —— 自动记录决策各阶段的执行耗时和结果
 *
 * <p>本类使用 Spring AOP 的 @Around 环绕通知，拦截所有标注了 {@link LogDecision} 注解的方法，
 * 自动采集以下信息：</p>
 * <ul>
 *   <li>方法执行耗时（毫秒）</li>
 *   <li>执行是否成功</li>
 *   <li>失败时的异常信息</li>
 * </ul>
 *
 * <p>设计模式：<b>AOP + 自定义注解</b></p>
 * <ul>
 *   <li>通过 {@link LogDecision} 注解声明式地标记需要记录日志的方法，对业务代码零侵入</li>
 *   <li>日志采集逻辑集中在切面中，遵循单一职责原则</li>
 *   <li>新增决策阶段时，只需在方法上添加 @LogDecision 注解即可自动获得日志能力</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code @LogDecision(stage = "rule_match")}
 * public MatchResult matchRules(DeviceData data) {
 *     // 业务逻辑...
 * }
 * </pre>
 *
 * @author Joseph Ho
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DecisionLogAspect {

    /** 决策日志服务，用于将采集到的日志持久化到 Elasticsearch */
    private final DecisionLogService decisionLogService;

    /**
     * 环绕通知：拦截 @LogDecision 注解标注的方法，记录执行耗时和结果
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>记录方法开始执行的时间戳</li>
     *   <li>执行目标方法（joinPoint.proceed()）</li>
     *   <li>无论成功还是异常，都在 finally 中计算耗时并记录日志</li>
     *   <li>如果方法抛出异常，捕获异常信息后重新抛出（不影响原有异常处理逻辑）</li>
     * </ol>
     *
     * @param joinPoint AOP 连接点，包含被拦截方法的签名、参数等信息
     * @return 被拦截方法的原始返回值
     * @throws Throwable 被拦截方法抛出的原始异常（切面不吞没异常）
     */
    @Around("@annotation(com.smart.infrastructure.log.LogDecision)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录方法开始执行的时间戳
        long start = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        String errorMsg = null;
        try {
            // 执行被拦截的目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            // 捕获异常信息，标记为执行失败
            success = false;
            errorMsg = e.getMessage();
            // 重新抛出异常，不影响上层的异常处理逻辑
            throw e;
        } finally {
            // 计算方法执行耗时
            long costMs = System.currentTimeMillis() - start;
            log.info("决策阶段执行: method={}, costMs={}, success={}", joinPoint.getSignature().getName(), costMs, success);
            // TODO: 构建 DecisionLog 并保存到 ES
        }
    }
}
