package com.smart.infrastructure.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 向量化服务（Embedding Service）—— RAG 管道的第三个环节
 *
 * <p>在 RAG 管道中，EmbeddingService 负责将文本内容转换为高维浮点向量（embedding），
 * 使得语义相似的文本在向量空间中距离更近，从而支持基于语义的相似度检索。</p>
 *
 * <p>核心原理：调用 Spring AI 提供的 EmbeddingModel（如 OpenAI text-embedding-ada-002、
 * 通义千问 embedding 等），将文本映射为固定维度的浮点数组。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li><b>入库阶段</b>：将知识文档的分片文本批量向量化后存入 pgvector</li>
 *   <li><b>检索阶段</b>：将用户查询文本向量化后，与存储的向量进行相似度计算</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
public class EmbeddingService {

    /**
     * 将单条文本转换为向量
     *
     * <p>用于检索阶段：将用户的查询文本转为向量，然后在向量数据库中进行相似度检索。</p>
     *
     * @param text 待向量化的文本
     * @return 文本对应的浮点向量数组
     */
    public float[] embed(String text) {
        // TODO: 调用 Spring AI EmbeddingModel 生成向量
        log.info("生成文本向量, 文本长度: {}", text != null ? text.length() : 0);
        return new float[0];
    }

    /**
     * 批量将文本转换为向量
     *
     * <p>用于入库阶段：将文档分片后的多个文本块批量向量化，减少网络调用次数，提升吞吐量。</p>
     *
     * @param texts 待向量化的文本列表
     * @return 每条文本对应的浮点向量数组列表（顺序与输入一一对应）
     */
    public List<float[]> embedBatch(List<String> texts) {
        // TODO: 批量调用 Embedding 接口
        log.info("批量生成向量, 数量: {}", texts != null ? texts.size() : 0);
        return List.of();
    }
}
