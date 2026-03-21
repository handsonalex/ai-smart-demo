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
 * 知识分片实体类，对应PostgreSQL数据库中的 t_knowledge_chunk 表。
 * <p>
 * 该实体是RAG知识库的核心存储单元，存放文档被切分后的文本片段及其向量嵌入。
 * 与其他实体不同，本实体存储在PostgreSQL中（而非MySQL），因为需要利用pgvector扩展
 * 进行高效的向量相似度检索。
 * </p>
 * <p>
 * 工作流程：
 * <ol>
 *   <li>文档（{@link KnowledgeDoc}）上传后，按固定长度或语义边界切分为多个分片</li>
 *   <li>每个分片的文本内容通过嵌入模型（如 text-embedding-ada-002）转换为向量</li>
 *   <li>文本和向量一起存储到本表中</li>
 *   <li>查询时，将用户问题也转换为向量，利用pgvector进行最近邻检索，找到最相关的分片</li>
 * </ol>
 * </p>
 *
 * @author Joseph Ho
 */
@Data
@TableName(value = "t_knowledge_chunk", autoResultMap = true)
public class KnowledgeChunk {

    /**
     * 分片唯一标识（主键）。
     * 使用雪花算法（ASSIGN_ID）生成分布式唯一ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属文档ID，关联 {@link KnowledgeDoc#id}，标识该分片来源于哪个文档 */
    private Long docId;

    /**
     * 分片在文档中的序号（从0开始）。
     * <p>
     * 用于保持分片的原始顺序，在需要展示上下文或拼接相邻分片时使用。
     * </p>
     */
    private Integer chunkIndex;

    /** 分片的文本内容，即从原始文档中切分出的一段文字，是向量检索命中后返回给LLM的上下文 */
    private String content;

    /**
     * 文本内容的向量嵌入表示（float数组）。
     * <p>
     * 该字段在PostgreSQL中以pgvector类型（vector）存储，通过 {@link PgVectorTypeHandler}
     * 实现Java float数组与pgvector字符串格式（如"[0.1,0.2,0.3]"）之间的互转。
     * 向量维度取决于所使用的嵌入模型（如OpenAI text-embedding-ada-002为1536维）。
     * pgvector支持基于余弦相似度、欧氏距离等算法进行高效的向量近邻检索。
     * </p>
     */
    @TableField(typeHandler = PgVectorTypeHandler.class)
    private float[] embedding;

    /**
     * 记录创建时间，仅在插入时自动填充。
     * 通过 MyBatis-Plus 的 {@link FieldFill#INSERT} 策略由框架自动设置。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
