package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.Device;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备 Mapper
 *
 * @author Joseph Ho
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
