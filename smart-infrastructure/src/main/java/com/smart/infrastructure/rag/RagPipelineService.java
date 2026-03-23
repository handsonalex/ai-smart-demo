package com.smart.infrastructure.rag;

import java.util.List;

/**
 * RAG 管道编排服务接口 —— 串联 RAG 全流程（入库 + 检索）
 *
 * @author Joseph Ho
 */
public interface RagPipelineService {

    /**
     * 执行完整的 RAG 入库流程：加载 -> 分片 -> 向量化 -> 存储
     *
     * @param docId    文档 ID
     * @param filePath 文档文件路径
     * @return 成功入库的分片数量
     */
    int ingest(Long docId, String filePath);

    /**
     * 检索与查询语义相关的知识片段
     *
     * @param query 查询文本
     * @param topK  返回最相似的前 K 个文本片段
     * @return 按相似度降序排列的文本片段列表
     */
    List<String> retrieve(String query, int topK);
}
