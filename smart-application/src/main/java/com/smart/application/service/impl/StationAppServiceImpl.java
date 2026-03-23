package com.smart.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.application.service.StationAppService;
import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.Station;
import com.smart.domain.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 电站应用服务 —— 提供电站（Station）的 CRUD 操作
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层（应用服务层）</b>，
 * 负责编排电站相关的业务用例。电站是整个智能决策系统的顶层管理单元，
 * 一个电站下可包含多个设备（Device）和多个智能场景（SmartScene）。</p>
 *
 * <p>本服务为简单的 CRUD 操作，直接委托给 MyBatis-Plus 的 Mapper 完成数据库操作，
 * 不涉及复杂的业务规则编排。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StationAppServiceImpl implements StationAppService {

    /** 电站 Mapper，基于 MyBatis-Plus 提供数据库访问能力 */
    private final StationMapper stationMapper;

    /**
     * 创建电站
     *
     * @param station 电站实体（由上层 Controller 转换 DTO 后传入）
     * @return 创建成功的电站实体（含数据库自动生成的 ID）
     */
    @Override
    public Station create(Station station) {
        stationMapper.insert(station);
        log.info("创建电站: {}", station.getId());
        return station;
    }

    /**
     * 根据 ID 查询电站
     *
     * @param id 电站ID
     * @return 电站实体
     * @throws BizException 当电站不存在时抛出 STATION_NOT_FOUND 业务异常
     */
    @Override
    public Station getById(Long id) {
        Station station = stationMapper.selectById(id);
        if (station == null) {
            throw new BizException(ErrorCode.STATION_NOT_FOUND);
        }
        return station;
    }

    /**
     * 分页查询电站列表，按创建时间倒序排列
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果，包含电站列表和总记录数
     */
    @Override
    public Page<Station> page(int pageNum, int pageSize) {
        return stationMapper.selectPage(
            new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<Station>().orderByDesc(Station::getCreateTime)
        );
    }

    /**
     * 更新电站信息
     *
     * @param station 包含更新字段的电站实体（必须携带 ID）
     */
    @Override
    public void update(Station station) {
        stationMapper.updateById(station);
        log.info("更新电站: {}", station.getId());
    }

    /**
     * 根据 ID 删除电站
     *
     * @param id 电站ID
     */
    @Override
    public void delete(Long id) {
        stationMapper.deleteById(id);
        log.info("删除电站: {}", id);
    }
}
