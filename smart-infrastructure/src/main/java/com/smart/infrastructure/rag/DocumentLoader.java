package com.smart.infrastructure.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 文档加载器
 */
@Slf4j
@Component
public class DocumentLoader {

    /**
     * 加载文档内容
     * @param filePath 文件路径
     * @return 文档文本内容
     */
    public String load(String filePath) {
        // TODO: 实现文档加载逻辑，支持 PDF、Word、TXT 等格式
        log.info("加载文档: {}", filePath);
        return "";
    }
}
