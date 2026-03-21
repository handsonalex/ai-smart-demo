package com.smart.infrastructure.rag;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本分片器 —— RAG 管道的第二个环节
 *
 * <p>在 RAG 管道中，TextSplitter 负责将文档加载器输出的长文本切分为较小的文本块（chunk），
 * 以便后续进行向量化和存储。</p>
 *
 * <p>分片策略：固定窗口 + 重叠（Sliding Window with Overlap）</p>
 * <ul>
 *   <li><b>固定窗口</b>：每个分片的最大字符数（默认 500 字符）</li>
 *   <li><b>重叠区域</b>：相邻分片之间的重叠字符数（默认 50 字符），目的是避免在分片边界处
 *       丢失上下文语义，确保跨分片边界的内容仍然能被完整检索到</li>
 * </ul>
 *
 * <p>示例：假设文本长度为 1000，chunkSize=500，overlap=50</p>
 * <ul>
 *   <li>分片1：字符 [0, 500)</li>
 *   <li>分片2：字符 [450, 950)  —— 与分片1重叠 50 个字符</li>
 *   <li>分片3：字符 [900, 1000) —— 与分片2重叠 50 个字符</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Component
public class TextSplitter {

    /** 默认分片大小（字符数） */
    private static final int DEFAULT_CHUNK_SIZE = 500;

    /** 默认重叠字符数，保证相邻分片之间的语义连续性 */
    private static final int DEFAULT_OVERLAP = 50;

    /**
     * 使用默认参数对文本进行分片
     *
     * @param text 待分片的原始文本
     * @return 分片后的文本列表
     */
    public List<String> split(String text) {
        return split(text, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    /**
     * 使用自定义参数对文本进行分片
     *
     * <p>采用滑动窗口算法：每次从 start 位置截取 chunkSize 长度的文本作为一个分片，
     * 然后窗口向后滑动 (chunkSize - overlap) 个字符，确保相邻分片之间有 overlap 个字符的重叠。</p>
     *
     * @param text      待分片的原始文本
     * @param chunkSize 每个分片的最大字符数
     * @param overlap   相邻分片之间的重叠字符数
     * @return 分片后的文本列表
     */
    public List<String> split(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        int start = 0;
        while (start < text.length()) {
            // 截取从 start 开始、长度为 chunkSize 的子串（末尾不足 chunkSize 时取到文本末尾）
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            // 窗口滑动步长 = chunkSize - overlap，保证相邻分片有 overlap 个字符的重叠
            start += chunkSize - overlap;
        }
        return chunks;
    }
}
