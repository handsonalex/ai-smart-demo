package com.smart.application.service;

import com.smart.domain.entity.SceneRule;
import com.smart.infrastructure.kafka.DeviceDataMessage;

import java.util.List;

/**
 * 规则引擎服务接口 —— 负责判断设备数据是否满足场景触发条件（AND 逻辑）
 *
 * @author Joseph Ho
 */
public interface RuleEngineService {

    /**
     * 判断设备数据是否满足所有规则条件（AND 逻辑）
     *
     * @param rules 场景关联的规则列表
     * @param data  设备上报的数据消息
     * @return true=所有规则都满足，false=至少有一条规则不满足或规则为空
     */
    boolean match(List<SceneRule> rules, DeviceDataMessage data);
}
