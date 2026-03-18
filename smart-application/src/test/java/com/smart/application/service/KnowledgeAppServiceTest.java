package com.smart.application.service;

import com.smart.common.exception.BizException;
import com.smart.domain.entity.KnowledgeDoc;
import com.smart.domain.mapper.KnowledgeDocMapper;
import com.smart.infrastructure.rag.RagPipelineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeAppServiceTest {

    @Mock
    private KnowledgeDocMapper knowledgeDocMapper;
    @Mock
    private RagPipelineService ragPipelineService;

    @InjectMocks
    private KnowledgeAppService knowledgeAppService;

    @Test
    @DisplayName("上传文档应触发 RAG 入库")
    void upload() {
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setDocName("测试文档");
        doc.setFilePath("/test/doc.pdf");
        when(ragPipelineService.ingest(any(), anyString())).thenReturn(10);

        KnowledgeDoc result = knowledgeAppService.upload(doc);
        assertEquals(10, result.getChunkCount());
        verify(knowledgeDocMapper).insert(any());
        verify(knowledgeDocMapper).updateById(any());
    }

    @Test
    @DisplayName("查询不存在的文档应抛出异常")
    void getByIdNotFound() {
        when(knowledgeDocMapper.selectById(999L)).thenReturn(null);
        assertThrows(BizException.class, () -> knowledgeAppService.getById(999L));
    }

    @Test
    @DisplayName("RAG 检索应调用管道服务")
    void retrieve() {
        when(ragPipelineService.retrieve("测试查询", 5)).thenReturn(List.of("片段1", "片段2"));
        List<String> result = knowledgeAppService.retrieve("测试查询", 5);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询所有文档")
    void listAll() {
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setId(1L);
        when(knowledgeDocMapper.selectList(null)).thenReturn(List.of(doc));
        List<KnowledgeDoc> result = knowledgeAppService.listAll();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("删除文档")
    void delete() {
        knowledgeAppService.delete(1L);
        verify(knowledgeDocMapper).deleteById(1L);
    }
}
