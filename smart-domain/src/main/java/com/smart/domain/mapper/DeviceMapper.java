package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.Device;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备数据访问层接口，对应MySQL数据库中的 t_device 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：按电站ID查询设备列表、按设备序列号查询设备、更新设备状态等。
 * </p>
 *
 * @author Joseph Ho
 * @see Device
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
