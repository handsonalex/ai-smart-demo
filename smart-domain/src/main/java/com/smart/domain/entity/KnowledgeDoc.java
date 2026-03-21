package com.smart.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文档元数据实体类，对应MySQL数据库中的 t_knowledge_doc 表。
 * <p>
 * 该实体是RAG（检索增强生成）知识库系统的文档管理层。
 * 用户上传的文档（PDF、Word、TXT等）在此记录元数据信息，文档内容经过分片处理后，
 * 每个分片（{@link KnowledgeChunk}）会存储在PostgreSQL中并生成向量嵌入（embedding）。
 * </p>
 * <p>
 * 系统在进行AI决策时，会通过向量相似度检索从知识库中找到与当前问题最相关的文档分片，
 * 将其作为上下文注入到LLM的Prompt中，从而实现基于领域知识的智能问答和决策辅助。
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName("t_knowledge_doc")
public class KnowledgeDoc {

    /**
     * 文档唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 文档名称，即上传时的原始文件名，如"光伏电站运维手册.pdf" */
    private String docName;

    /**
     * 文档类型，标识文件格式。
     * <p>
     * 典型取值："pdf"、"docx"、"txt"、"md"等。
     * 不同文档类型使用不同的解析器提取文本内容。
     * </p>
     */
    private String docType;

    /** 文档在服务器上的存储路径，用于后续需要时重新读取原始文件 */
    private String filePath;

    /**
     * 文档被切分后的分片总数。
     * <p>
     * 记录该文档经过文本分片（chunking）后产生的 {@link KnowledgeChunk} 数量，
     * 方便在界面上展示文档的处理结果，也用于数据一致性校验。
     * </p>
     */
    private Integer chunkCount;

    /**
     * 文档处理状态。
     * <p>
     * 典型取值：0-待处理、1-处理中（正在分片和向量化）、2-处理完成、3-处理失败。
     * 文档上传后需要经过异步的分片和向量嵌入处理，通过状态跟踪处理进度。
     * </p>
     */
    private Integer status;

    /**
     * 记录创建时间，仅在插入时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间，在插入和更新时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT_UPDATE} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
