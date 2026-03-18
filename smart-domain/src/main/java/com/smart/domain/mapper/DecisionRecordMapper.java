package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.DecisionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 决策记录 Mapper
 *
 * @author Joseph Ho
 */
@Mapper
public interface DecisionRecordMapper extends BaseMapper<DecisionRecord> {
}
