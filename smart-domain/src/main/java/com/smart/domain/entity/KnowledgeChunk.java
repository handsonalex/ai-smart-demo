package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.domain.handler.PgVectorTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识分片 (PostgreSQL pgvector)
 *
 * @author Joseph Ho
 */
@Data
@TableName(value = "t_knowledge_chunk", autoResultMap = true)
public class KnowledgeChunk {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long docId;

    private Integer chunkIndex;

    private String content;

    @TableField(typeHandler = PgVectorTypeHandler.class)
    private float[] embedding;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
