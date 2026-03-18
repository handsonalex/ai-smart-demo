package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识文档 Mapper
 *
 * @author Joseph Ho
 */
@Mapper
public interface KnowledgeDocMapper extends BaseMapper<KnowledgeDoc> {
}
