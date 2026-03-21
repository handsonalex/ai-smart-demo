package com.smart.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 *
 * <p>本项目集成 Spring AI 框架，为智能决策引擎提供大语言模型（LLM）和向量化（Embedding）能力。
 * 主要使用场景：</p>
 * <ul>
 *   <li>EmbeddingModel：将知识文档和用户查询文本转换为向量，用于 RAG 检索</li>
 *   <li>ChatModel：调用大模型进行智能决策推理，生成决策建议</li>
 * </ul>
 *
 * <p>Spring AI 相关配置（如模型端点地址、API Key、模型名称、温度参数等）通过 application.yml 中的
 * spring.ai.* 属性由 Spring Boot 自动配置完成，因此本类暂无需额外的 Bean 定义。</p>
 *
 * <p>如果后续需要自定义 Prompt 模板、RAG Advisor、Function Calling 等高级特性，可在本类中扩展。</p>
 *
 * @author Joseph Ho
 */
@Configuration
public class SpringAiConfig {
}
