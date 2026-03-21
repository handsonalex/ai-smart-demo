package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.DeviceData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备上报数据的数据访问层接口，对应MySQL数据库中的 t_device_data 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：批量插入设备上报数据、按设备ID和时间范围查询历史数据、获取最新一条数据等。
 * </p>
 * <p>
 * 注意：该表数据量增长较快，生产环境中可能需要添加自定义SQL进行数据归档或分页优化。
 * </p>
 *
 * @author Joseph Ho
 * @see DeviceData
 */
@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData> {
}
