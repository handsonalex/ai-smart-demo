package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.DecisionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI决策记录数据访问层接口，对应MySQL数据库中的 t_decision_record 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：记录AI决策结果、按电站和时间范围查询决策历史、更新决策执行状态等。
 * </p>
 *
 * @author Joseph Ho
 * @see DecisionRecord
 */
@Mapper
public interface DecisionRecordMapper extends BaseMapper<DecisionRecord> {
}
