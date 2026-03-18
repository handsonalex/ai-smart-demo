package com.smart.application.service;

import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.KnowledgeDoc;
import com.smart.domain.mapper.KnowledgeDocMapper;
import com.smart.infrastructure.rag.RagPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库应用服务
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeAppService {

    private final KnowledgeDocMapper knowledgeDocMapper;
    private final RagPipelineService ragPipelineService;

    /**
     * 上传知识文档并触发 RAG 入库
     */
    public KnowledgeDoc upload(KnowledgeDoc doc) {
        knowledgeDocMapper.insert(doc);
        log.info("上传知识文档: {}", doc.getId());
        // 异步触发 RAG 入库
        int chunkCount = ragPipelineService.ingest(doc.getId(), doc.getFilePath());
        doc.setChunkCount(chunkCount);
        knowledgeDocMapper.updateById(doc);
        return doc;
    }

    public KnowledgeDoc getById(Long id) {
        KnowledgeDoc doc = knowledgeDocMapper.selectById(id);
        if (doc == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return doc;
    }

    public List<KnowledgeDoc> listAll() {
        return knowledgeDocMapper.selectList(null);
    }

    /**
     * RAG 检索
     */
    public List<String> retrieve(String query, int topK) {
        return ragPipelineService.retrieve(query, topK);
    }

    public void delete(Long id) {
        knowledgeDocMapper.deleteById(id);
        log.info("删除知识文档: {}", id);
    }
}
