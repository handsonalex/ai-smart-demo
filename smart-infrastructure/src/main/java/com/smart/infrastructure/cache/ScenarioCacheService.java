package com.smart.infrastructure.cache;

import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;

import java.util.List;

/**
 * 场景缓存服务接口（Cache-Aside 模式）
 *
 * @author Joseph Ho
 */
public interface ScenarioCacheService {

    List<SmartScene> getEnabledScenes(Long stationId);

    List<SceneRule> getSceneRules(Long sceneId);

    void evictStationScenes(Long stationId);

    void evictSceneRules(Long sceneId);
}
