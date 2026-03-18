package com.smart.infrastructure.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * RAG 管道服务：文档加载 -> 分片 -> 向量化 -> 存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagPipelineService {

    private final DocumentLoader documentLoader;
    private final TextSplitter textSplitter;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    /**
     * 执行完整 RAG 入库流程
     */
    public int ingest(Long docId, String filePath) {
        // TODO: 实现完整的 RAG 入库流程
        // 1. 加载文档
        // 2. 文本分片
        // 3. 向量化
        // 4. 存储到向量数据库
        log.info("开始 RAG 入库流程, docId: {}, filePath: {}", docId, filePath);
        return 0;
    }

    /**
     * 检索相关知识片段
     */
    public List<String> retrieve(String query, int topK) {
        // TODO: 实现 RAG 检索流程
        // 1. query 向量化
        // 2. 向量相似度检索
        // 3. 返回相关文本片段
        log.info("RAG 检索, query: {}, topK: {}", query, topK);
        return List.of();
    }
}
