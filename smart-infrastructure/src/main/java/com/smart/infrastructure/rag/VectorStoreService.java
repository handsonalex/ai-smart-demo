package com.smart.infrastructure.rag;

import com.smart.domain.entity.KnowledgeChunk;
import com.smart.domain.mapper.chunk.KnowledgeChunkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 向量存储服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final KnowledgeChunkMapper knowledgeChunkMapper;

    /**
     * 存储向量
     */
    public void store(Long docId, int chunkIndex, String content, float[] embedding) {
        // TODO: 保存分片及其向量到 PostgreSQL pgvector
        log.info("存储向量, docId: {}, chunkIndex: {}", docId, chunkIndex);
    }

    /**
     * 相似度检索
     */
    public List<KnowledgeChunk> search(float[] queryEmbedding, int topK) {
        // TODO: 使用 pgvector 进行向量相似度检索
        log.info("向量检索, topK: {}", topK);
        return List.of();
    }
}
