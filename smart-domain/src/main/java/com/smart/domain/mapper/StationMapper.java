package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.Station;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电站 Mapper
 *
 * @author Joseph Ho
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {
}
