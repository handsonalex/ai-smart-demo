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

@Tag(name = "知识库管理")
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeAppService knowledgeAppService;

    @Operation(summary = "上传知识文档")
    @PostMapping("/upload")
    public Result<KnowledgeDoc> upload(@Valid @RequestBody KnowledgeUploadReq req) {
        KnowledgeDoc doc = new KnowledgeDoc();
        BeanUtils.copyProperties(req, doc);
        return Result.success(knowledgeAppService.upload(doc));
    }

    @Operation(summary = "查询知识文档详情")
    @GetMapping("/{id}")
    public Result<KnowledgeDoc> getById(@PathVariable Long id) {
        return Result.success(knowledgeAppService.getById(id));
    }

    @Operation(summary = "查询所有知识文档")
    @GetMapping
    public Result<List<KnowledgeDoc>> listAll() {
        return Result.success(knowledgeAppService.listAll());
    }

    @Operation(summary = "RAG 检索")
    @GetMapping("/retrieve")
    public Result<List<String>> retrieve(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") Integer topK) {
        return Result.success(knowledgeAppService.retrieve(query, topK));
    }

    @Operation(summary = "删除知识文档")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeAppService.delete(id);
        return Result.success();
    }
}
