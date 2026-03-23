package com.smart.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.domain.entity.Device;

import java.util.List;

/**
 * 设备应用服务接口 —— 提供设备（Device）的 CRUD 操作
 *
 * @author Joseph Ho
 */
public interface DeviceAppService {

    Device create(Device device);

    Device getById(Long id);

    List<Device> listByStationId(Long stationId);

    Page<Device> page(int pageNum, int pageSize);

    void update(Device device);

    void delete(Long id);
}
