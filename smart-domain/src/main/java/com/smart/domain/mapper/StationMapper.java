package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.Station;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电站数据访问层接口，对应MySQL数据库中的 t_station 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力，
 * 无需手写XML映射文件即可完成常见的数据库操作。
 * </p>
 * <p>
 * 如需自定义复杂SQL（如多表联查、统计聚合），可在此接口中声明方法并在对应的XML文件中编写SQL。
 * </p>
 *
 * @author Joseph Ho
 * @see Station
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {
}
