package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 智能决策系统启动类
 *
 * <p>该类是整个 Spring Boot 应用的入口点。
 * {@link SpringBootApplication} 是一个组合注解，等价于同时使用以下三个注解：
 * <ul>
 *   <li>{@code @SpringBootConfiguration} — 标记当前类为配置类</li>
 *   <li>{@code @EnableAutoConfiguration} — 开启 Spring Boot 自动装配机制，根据 classpath 中的依赖自动配置 Bean</li>
 *   <li>{@code @ComponentScan} — 自动扫描当前包（com.smart）及其子包下的所有组件（@Component、@Service、@Controller 等）</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@SpringBootApplication
public class AiSmartDemoApplication {

    /**
     * 应用程序主入口方法
     *
     * @param args 命令行启动参数，可通过 {@code --key=value} 的形式传递配置项覆盖默认值
     */
    public static void main(String[] args) {
        SpringApplication.run(AiSmartDemoApplication.class, args);
    }
}
