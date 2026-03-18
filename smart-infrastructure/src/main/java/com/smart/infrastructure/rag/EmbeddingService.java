package com.smart.infrastructure.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Embedding 向量化服务
 */
@Slf4j
@Service
public class EmbeddingService {

    /**
     * 将文本转换为向量
     */
    public float[] embed(String text) {
        // TODO: 调用 Spring AI EmbeddingClient 生成向量
        log.info("生成文本向量, 文本长度: {}", text != null ? text.length() : 0);
        return new float[0];
    }

    /**
     * 批量将文本转换为向量
     */
    public List<float[]> embedBatch(List<String> texts) {
        // TODO: 批量调用 Embedding 接口
        log.info("批量生成向量, 数量: {}", texts != null ? texts.size() : 0);
        return List.of();
    }
}
