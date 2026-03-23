package com.smart.application.service;

import com.smart.domain.entity.DecisionRecord;
import com.smart.infrastructure.kafka.DeviceDataMessage;

/**
 * 决策应用服务接口 —— AI 智能决策系统的核心编排层
 *
 * @author Joseph Ho
 */
public interface DecisionAppService {

    /**
     * 处理设备上报数据，触发完整的 AI 决策流程
     *
     * @param data 设备上报的数据消息
     */
    void processDeviceData(DeviceDataMessage data);

    /**
     * 根据决策ID查询决策记录（Cache-Aside 模式）
     *
     * @param id 决策记录ID
     * @return 决策记录实体
     */
    DecisionRecord getById(Long id);
}
