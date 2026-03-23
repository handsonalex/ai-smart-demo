package com.smart.application.service;

import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;

import java.util.List;

/**
 * 场景应用服务接口 —— 管理智能场景（SmartScene）及其关联规则（SceneRule）的生命周期
 *
 * @author Joseph Ho
 */
public interface SceneAppService {

    SmartScene create(SmartScene scene, List<SceneRule> rules);

    SmartScene getById(Long id);

    List<SceneRule> getRulesBySceneId(Long sceneId);

    List<SmartScene> listByStationId(Long stationId);

    void delete(Long id);
}
