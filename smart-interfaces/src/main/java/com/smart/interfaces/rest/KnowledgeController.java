package com.smart.interfaces.rest;

import com.smart.application.service.KnowledgeAppService;
import com.smart.common.result.Result;
import com.smart.domain.entity.KnowledgeDoc;
import com.smart.interfaces.dto.request.KnowledgeUploadReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理控制器 —— 接口层（Interfaces Layer）
 *
 * <p>提供知识文档的管理接口和 RAG（检索增强生成）检索接口。
 * 知识库是 AI 决策引擎的重要数据来源，通过向量化存储文档内容，
 * 支持语义检索，为 AI 大模型提供领域知识上下文。
 * <ul>
 *   <li>POST   /api/v1/knowledge/upload    — 上传知识文档</li>
 *   <li>GET    /api/v1/knowledge/{id}      — 查询知识文档详情</li>
 *   <li>GET    /api/v1/knowledge           — 查询所有知识文档</li>
 *   <li>GET    /api/v1/knowledge/retrieve  — RAG 语义检索</li>
 *   <li>DELETE /api/v1/knowledge/{id}      — 删除知识文档</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>上传接口负责接收文档元数据，实际的文档解析和向量化在应用服务层完成</li>
 *   <li>RAG 检索接口接受自然语言查询，返回最相关的 topK 条文档片段</li>
 *   <li>注意：此 Controller 直接返回领域实体 {@link KnowledgeDoc}，未做 DTO 转换，
 *       是一种简化处理，适用于领域模型与接口模型一致的场景</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "知识库管理")
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    /** 知识库应用服务，负责文档管理和 RAG 检索的业务编排 */
    private final KnowledgeAppService knowledgeAppService;

    /**
     * 上传知识文档
     *
     * <p>处理流程：参数校验 → DTO 转领域对象 → 调用应用服务上传（含文档解析和向量化）
     *
     * @param req 上传请求，包含文档名称、类型和文件路径
     * @return 上传成功后的知识文档信息（含生成的 ID 等）
     */
    @Operation(summary = "上传知识文档")
    @PostMapping("/upload")
    public Result<KnowledgeDoc> upload(@Valid @RequestBody KnowledgeUploadReq req) {
        KnowledgeDoc doc = new KnowledgeDoc();
        BeanUtils.copyProperties(req, doc);
        return Result.success(knowledgeAppService.upload(doc));
    }

    /**
     * 查询知识文档详情
     *
     * @param id 知识文档主键 ID
     * @return 知识文档详细信息
     */
    @Operation(summary = "查询知识文档详情")
    @GetMapping("/{id}")
    public Result<KnowledgeDoc> getById(@PathVariable Long id) {
        return Result.success(knowledgeAppService.getById(id));
    }

    /**
     * 查询所有知识文档列表
     *
     * <p>返回系统中所有已上传的知识文档，无分页（适用于文档数量较少的场景）
     *
     * @return 所有知识文档列表
     */
    @Operation(summary = "查询所有知识文档")
    @GetMapping
    public Result<List<KnowledgeDoc>> listAll() {
        return Result.success(knowledgeAppService.listAll());
    }

    /**
     * RAG（检索增强生成）语义检索
     *
     * <p>基于用户输入的自然语言查询，通过向量相似度匹配，
     * 从知识库中检索最相关的文档片段，供 AI 大模型作为上下文参考。
     * 这是 RAG 架构中"检索"环节的入口。
     *
     * @param query 自然语言查询文本
     * @param topK  返回最相关的前 K 条结果，默认值为 5
     * @return 匹配的文档片段列表
     */
    @Operation(summary = "RAG 检索")
    @GetMapping("/retrieve")
    public Result<List<String>> retrieve(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") Integer topK) {
        return Result.success(knowledgeAppService.retrieve(query, topK));
    }

    /**
     * 删除知识文档
     *
     * <p>删除指定的知识文档及其对应的向量索引数据
     *
     * @param id 要删除的知识文档 ID
     * @return 空响应，表示删除成功
     */
    @Operation(summary = "删除知识文档")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeAppService.delete(id);
        return Result.success();
    }
}
