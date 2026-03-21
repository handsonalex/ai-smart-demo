package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 上传知识文档请求 DTO
 *
 * <p>用于接收前端上传知识文档时提交的参数。知识文档将被解析、分块、向量化后
 * 存入向量数据库，供 RAG（检索增强生成）检索使用。
 *
 * <p>支持的文档类型：PDF、WORD、TXT
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "上传知识文档请求")
public class KnowledgeUploadReq {
    /** 文档名称，必填，用于标识和检索文档 */
    @NotBlank(message = "文档名称不能为空")
    @Schema(description = "文档名称")
    private String docName;

    /** 文档类型，选填，支持 PDF/WORD/TXT 三种格式 */
    @Schema(description = "文档类型: PDF/WORD/TXT")
    private String docType;

    /** 文件路径，必填，指向待上传文档的存储路径 */
    @NotBlank(message = "文件路径不能为空")
    @Schema(description = "文件路径")
    private String filePath;
}
