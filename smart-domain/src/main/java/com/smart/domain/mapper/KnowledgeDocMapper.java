package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识文档元数据的数据访问层接口，对应MySQL数据库中的 t_knowledge_doc 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：上传文档时创建记录、更新文档处理状态和分片数量、查询文档列表等。
 * </p>
 *
 * @author Joseph Ho
 * @see KnowledgeDoc
 */
@Mapper
public interface KnowledgeDocMapper extends BaseMapper<KnowledgeDoc> {
}
