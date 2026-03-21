package com.smart.domain.mapper.chunk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.KnowledgeChunk;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识分片数据访问层接口，对应PostgreSQL数据库中的 t_knowledge_chunk 表。
 * <p>
 * 与其他Mapper不同，本Mapper操作的是PostgreSQL数据库（而非MySQL），
 * 因为知识分片需要利用pgvector扩展进行向量相似度检索。
 * 在多数据源配置中，本Mapper需要绑定到PostgreSQL的SqlSessionFactory。
 * </p>
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得基本CRUD能力。
 * 对于向量相似度检索等pgvector特有操作，通常需要在XML映射文件中编写自定义SQL，
 * 使用pgvector的距离操作符（如 {@code <=>} 余弦距离）进行最近邻查询。
 * </p>
 *
 * @author Joseph Ho
 * @see KnowledgeChunk
 */
@Mapper
public interface KnowledgeChunkMapper extends BaseMapper<KnowledgeChunk> {
}
