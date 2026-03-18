package com.smart.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.Station;
import com.smart.domain.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationAppService {

    private final StationMapper stationMapper;

    public Station create(Station station) {
        stationMapper.insert(station);
        log.info("创建电站: {}", station.getId());
        return station;
    }

    public Station getById(Long id) {
        Station station = stationMapper.selectById(id);
        if (station == null) {
            throw new BizException(ErrorCode.STATION_NOT_FOUND);
        }
        return station;
    }

    public Page<Station> page(int pageNum, int pageSize) {
        return stationMapper.selectPage(
            new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<Station>().orderByDesc(Station::getCreateTime)
        );
    }

    public void update(Station station) {
        stationMapper.updateById(station);
        log.info("更新电站: {}", station.getId());
    }

    public void delete(Long id) {
        stationMapper.deleteById(id);
        log.info("删除电站: {}", id);
    }
}
