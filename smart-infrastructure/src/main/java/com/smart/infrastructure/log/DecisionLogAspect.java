package com.smart.infrastructure.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 决策日志切面：自动记录决策各阶段耗时
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DecisionLogAspect {

    private final DecisionLogService decisionLogService;

    @Around("@annotation(com.smart.infrastructure.log.LogDecision)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        String errorMsg = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            success = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long costMs = System.currentTimeMillis() - start;
            log.info("决策阶段执行: method={}, costMs={}, success={}", joinPoint.getSignature().getName(), costMs, success);
            // TODO: 构建 DecisionLog 并保存到 ES
        }
    }
}
