package com.smart.infrastructure.rag;

import com.smart.domain.entity.KnowledgeChunk;

import java.util.List;

/**
 * 向量存储服务接口 —— RAG 管道的第四个环节
 *
 * @author Joseph Ho
 */
public interface VectorStoreService {

    void store(Long docId, int chunkIndex, String content, float[] embedding);

    List<KnowledgeChunk> search(float[] queryEmbedding, int topK);
}
