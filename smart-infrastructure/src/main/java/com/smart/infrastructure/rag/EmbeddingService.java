package com.smart.infrastructure.rag;

import java.util.List;

/**
 * 向量化服务接口（Embedding Service）—— RAG 管道的第三个环节
 *
 * @author Joseph Ho
 */
public interface EmbeddingService {

    float[] embed(String text);

    List<float[]> embedBatch(List<String> texts);
}
