package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.SmartScene;
import org.apache.ibatis.annotations.Mapper;

/**
 * 智能场景数据访问层接口，对应MySQL数据库中的 t_smart_scene 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：按电站ID查询已启用的场景列表、按优先级排序获取场景等。
 * </p>
 *
 * @author Joseph Ho
 * @see SmartScene
 */
@Mapper
public interface SmartSceneMapper extends BaseMapper<SmartScene> {
}
