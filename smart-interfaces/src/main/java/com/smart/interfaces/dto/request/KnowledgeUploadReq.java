package com.smart.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 上传知识文档请求
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "上传知识文档请求")
public class KnowledgeUploadReq {
    @NotBlank(message = "文档名称不能为空")
    @Schema(description = "文档名称")
    private String docName;

    @Schema(description = "文档类型: PDF/WORD/TXT")
    private String docType;

    @NotBlank(message = "文件路径不能为空")
    @Schema(description = "文件路径")
    private String filePath;
}
