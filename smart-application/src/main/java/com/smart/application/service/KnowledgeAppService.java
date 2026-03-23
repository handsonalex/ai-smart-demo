package com.smart.application.service;

import com.smart.domain.entity.KnowledgeDoc;

import java.util.List;

/**
 * 知识库应用服务接口 —— 管理知识文档的上传、检索和生命周期
 *
 * @author Joseph Ho
 */
public interface KnowledgeAppService {

    KnowledgeDoc upload(KnowledgeDoc doc);

    KnowledgeDoc getById(Long id);

    List<KnowledgeDoc> listAll();

    List<String> retrieve(String query, int topK);

    void delete(Long id);
}
