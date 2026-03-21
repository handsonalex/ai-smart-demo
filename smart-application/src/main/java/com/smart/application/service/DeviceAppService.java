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
 * 设备应用服务 —— 提供设备（Device）的 CRUD 操作
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层（应用服务层）</b>，
 * 负责编排设备相关的业务用例。设备隶属于电站（Station），是数据采集的最小单元，
 * 通过 Kafka 上报功率、温度、SOC 等实时数据，触发 AI 决策流程。</p>
 *
 * <p>本服务为简单的 CRUD 操作，支持按电站维度查询设备列表，
 * 直接委托给 MyBatis-Plus 的 Mapper 完成数据库操作。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAppService {

    /** 设备 Mapper，基于 MyBatis-Plus 提供数据库访问能力 */
    private final DeviceMapper deviceMapper;

    /**
     * 创建设备
     *
     * @param device 设备实体（由上层 Controller 转换 DTO 后传入）
     * @return 创建成功的设备实体（含数据库自动生成的 ID）
     */
    public Device create(Device device) {
        deviceMapper.insert(device);
        log.info("创建设备: {}", device.getId());
        return device;
    }

    /**
     * 根据 ID 查询设备
     *
     * @param id 设备ID
     * @return 设备实体
     * @throws BizException 当设备不存在时抛出 DEVICE_NOT_FOUND 业务异常
     */
    public Device getById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BizException(ErrorCode.DEVICE_NOT_FOUND);
        }
        return device;
    }

    /**
     * 根据电站 ID 查询其下所有设备
     *
     * <p>用于在电站详情页展示该电站的设备列表。</p>
     *
     * @param stationId 电站ID
     * @return 该电站下的设备列表
     */
    public List<Device> listByStationId(Long stationId) {
        return deviceMapper.selectList(
            new LambdaQueryWrapper<Device>().eq(Device::getStationId, stationId)
        );
    }

    /**
     * 分页查询设备列表，按创建时间倒序排列
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果，包含设备列表和总记录数
     */
    public Page<Device> page(int pageNum, int pageSize) {
        return deviceMapper.selectPage(
            new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<Device>().orderByDesc(Device::getCreateTime)
        );
    }

    /**
     * 更新设备信息
     *
     * @param device 包含更新字段的设备实体（必须携带 ID）
     */
    public void update(Device device) {
        deviceMapper.updateById(device);
    }

    /**
     * 根据 ID 删除设备
     *
     * @param id 设备ID
     */
    public void delete(Long id) {
        deviceMapper.deleteById(id);
    }
}
