package com.smart.infrastructure.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 决策日志注解
 *
 * @author Joseph Ho
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDecision {
    /**
     * 决策阶段
     */
    String stage() default "";
}
