package com.smart.infrastructure.rag;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本分片器
 *
 * @author Joseph Ho
 */
@Component
public class TextSplitter {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_OVERLAP = 50;

    /**
     * 将文本按固定大小分片
     */
    public List<String> split(String text) {
        return split(text, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    /**
     * 将文本按指定大小和重叠量分片
     */
    public List<String> split(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start += chunkSize - overlap;
        }
        return chunks;
    }
}
