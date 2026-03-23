package com.smart.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.domain.entity.Station;

/**
 * 电站应用服务接口 —— 提供电站（Station）的 CRUD 操作
 *
 * @author Joseph Ho
 */
public interface StationAppService {

    Station create(Station station);

    Station getById(Long id);

    Page<Station> page(int pageNum, int pageSize);

    void update(Station station);

    void delete(Long id);
}
