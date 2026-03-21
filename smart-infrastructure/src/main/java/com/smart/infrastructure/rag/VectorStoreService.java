package com.smart.infrastructure.rag;

import com.smart.domain.entity.KnowledgeChunk;
import com.smart.domain.mapper.chunk.KnowledgeChunkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 向量存储服务 —— RAG 管道的第四个环节
 *
 * <p>在 RAG 管道中，VectorStoreService 负责将向量化后的文本分片及其向量持久化到 PostgreSQL（pgvector），
 * 并提供基于向量相似度的检索能力。</p>
 *
 * <p>存储方案：使用 PostgreSQL 的 pgvector 扩展，它支持在 SQL 数据库中原生存储和检索向量数据，
 * 相比独立的向量数据库（如 Milvus、Pinecone），pgvector 的优势是：</p>
 * <ul>
 *   <li>与业务数据共享同一数据库基础设施，运维成本低</li>
 *   <li>支持事务，保证数据一致性</li>
 *   <li>支持 SQL 查询与向量检索的混合使用</li>
 * </ul>
 *
 * <p>数据通过 {@link KnowledgeChunkMapper} 操作，该 Mapper 绑定到 PostgreSQL 数据源
 * （见 {@link com.smart.infrastructure.config.PostgresDataSourceConfig}）。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    /** 知识分片 Mapper，操作 PostgreSQL 中的知识分片表 */
    private final KnowledgeChunkMapper knowledgeChunkMapper;

    /**
     * 存储文本分片及其向量到 pgvector
     *
     * @param docId      关联的文档 ID
     * @param chunkIndex 分片在文档中的序号（从 0 开始），用于保持分片的原始顺序
     * @param content    分片的原始文本内容
     * @param embedding  分片文本对应的向量（由 EmbeddingService 生成）
     */
    public void store(Long docId, int chunkIndex, String content, float[] embedding) {
        // TODO: 保存分片及其向量到 PostgreSQL pgvector
        log.info("存储向量, docId: {}, chunkIndex: {}", docId, chunkIndex);
    }

    /**
     * 基于向量相似度检索最相关的知识分片
     *
     * <p>使用 pgvector 的相似度运算符（如余弦相似度 <=>）在向量空间中查找与查询向量最接近的 topK 个分片。</p>
     *
     * @param queryEmbedding 查询文本的向量（由 EmbeddingService 对用户查询文本向量化得到）
     * @param topK           返回最相似的前 K 个分片
     * @return 按相似度降序排列的知识分片列表
     */
    public List<KnowledgeChunk> search(float[] queryEmbedding, int topK) {
        // TODO: 使用 pgvector 进行向量相似度检索
        log.info("向量检索, topK: {}", topK);
        return List.of();
    }
}
