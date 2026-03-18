package com.smart.infrastructure.cache;

import com.smart.common.constants.CacheExpire;
import com.smart.common.constants.RedisKeys;
import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.domain.mapper.SceneRuleMapper;
import com.smart.domain.mapper.SmartSceneMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 场景缓存服务
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioCacheService {

    private final RedisService redisService;
    private final SmartSceneMapper smartSceneMapper;
    private final SceneRuleMapper sceneRuleMapper;

    /**
     * 获取电站下所有启用的场景
     */
    public List<SmartScene> getEnabledScenes(Long stationId) {
        String key = RedisKeys.SCENARIO_PREFIX + stationId;
        List<SmartScene> scenes = redisService.get(key);
        if (scenes != null) {
            return scenes;
        }
        scenes = smartSceneMapper.selectList(
            new LambdaQueryWrapper<SmartScene>()
                .eq(SmartScene::getStationId, stationId)
                .eq(SmartScene::getEnabled, true)
                .orderByAsc(SmartScene::getPriority)
        );
        if (scenes == null) {
            scenes = Collections.emptyList();
        }
        redisService.set(key, scenes, CacheExpire.TEN_MINUTES, TimeUnit.SECONDS);
        return scenes;
    }

    /**
     * 获取场景下的所有规则
     */
    public List<SceneRule> getSceneRules(Long sceneId) {
        String key = RedisKeys.SCENARIO_PREFIX + "rules:" + sceneId;
        List<SceneRule> rules = redisService.get(key);
        if (rules != null) {
            return rules;
        }
        rules = sceneRuleMapper.selectList(
            new LambdaQueryWrapper<SceneRule>()
                .eq(SceneRule::getSceneId, sceneId)
        );
        if (rules == null) {
            rules = Collections.emptyList();
        }
        redisService.set(key, rules, CacheExpire.TEN_MINUTES, TimeUnit.SECONDS);
        return rules;
    }

    /**
     * 清除电站场景缓存
     */
    public void evictStationScenes(Long stationId) {
        String key = RedisKeys.SCENARIO_PREFIX + stationId;
        redisService.delete(key);
        log.info("清除电站场景缓存, stationId: {}", stationId);
    }

    /**
     * 清除场景规则缓存
     */
    public void evictSceneRules(Long sceneId) {
        String key = RedisKeys.SCENARIO_PREFIX + "rules:" + sceneId;
        redisService.delete(key);
        log.info("清除场景规则缓存, sceneId: {}", sceneId);
    }
}
