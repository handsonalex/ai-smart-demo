package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.SceneRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场景规则数据访问层接口，对应MySQL数据库中的 t_scene_rule 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：按场景ID查询该场景下的所有规则，用于设备数据的条件匹配和触发判断。
 * </p>
 *
 * @author Joseph Ho
 * @see SceneRule
 */
@Mapper
public interface SceneRuleMapper extends BaseMapper<SceneRule> {
}
