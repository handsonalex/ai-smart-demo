package com.smart.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置类
 *
 * <p>本项目使用 Elasticsearch 作为决策日志的存储引擎，利用其强大的全文检索和聚合分析能力，
 * 实现决策过程的可观测性。主要存储内容包括：</p>
 * <ul>
 *   <li>决策各阶段（规则匹配、AI 推理等）的输入输出</li>
 *   <li>每个阶段的耗时统计</li>
 *   <li>异常信息记录</li>
 * </ul>
 *
 * <p>ES 客户端连接配置（如 uris、username、password 等）通过 application.yml 中的
 * spring.elasticsearch.* 属性由 Spring Boot 自动配置完成，因此本类暂无需额外的 Bean 定义。</p>
 *
 * <p>如果后续需要自定义索引映射、RestHighLevelClient 配置或索引生命周期策略（ILM），
 * 可在本类中扩展。</p>
 *
 * @author Joseph Ho
 */
@Configuration
public class ElasticsearchConfig {
}
