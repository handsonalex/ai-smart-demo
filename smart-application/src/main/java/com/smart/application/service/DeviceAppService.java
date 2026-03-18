package com.smart.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.Device;
import com.smart.domain.mapper.DeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备应用服务
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAppService {

    private final DeviceMapper deviceMapper;

    public Device create(Device device) {
        deviceMapper.insert(device);
        log.info("创建设备: {}", device.getId());
        return device;
    }

    public Device getById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BizException(ErrorCode.DEVICE_NOT_FOUND);
        }
        return device;
    }

    public List<Device> listByStationId(Long stationId) {
        return deviceMapper.selectList(
            new LambdaQueryWrapper<Device>().eq(Device::getStationId, stationId)
        );
    }

    public Page<Device> page(int pageNum, int pageSize) {
        return deviceMapper.selectPage(
            new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<Device>().orderByDesc(Device::getCreateTime)
        );
    }

    public void update(Device device) {
        deviceMapper.updateById(device);
    }

    public void delete(Long id) {
        deviceMapper.deleteById(id);
    }
}
