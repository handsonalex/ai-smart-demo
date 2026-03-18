package com.smart.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.domain.mapper.SceneRuleMapper;
import com.smart.domain.mapper.SmartSceneMapper;
import com.smart.infrastructure.cache.ScenarioCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SceneAppService {

    private final SmartSceneMapper smartSceneMapper;
    private final SceneRuleMapper sceneRuleMapper;
    private final ScenarioCacheService scenarioCacheService;

    @Transactional(rollbackFor = Exception.class)
    public SmartScene create(SmartScene scene, List<SceneRule> rules) {
        smartSceneMapper.insert(scene);
        if (rules != null) {
            for (SceneRule rule : rules) {
                rule.setSceneId(scene.getId());
                sceneRuleMapper.insert(rule);
            }
        }
        scenarioCacheService.evictStationScenes(scene.getStationId());
        log.info("创建场景: {}, 规则数: {}", scene.getId(), rules != null ? rules.size() : 0);
        return scene;
    }

    public SmartScene getById(Long id) {
        SmartScene scene = smartSceneMapper.selectById(id);
        if (scene == null) {
            throw new BizException(ErrorCode.SCENE_NOT_FOUND);
        }
        return scene;
    }

    public List<SceneRule> getRulesBySceneId(Long sceneId) {
        return sceneRuleMapper.selectList(
            new LambdaQueryWrapper<SceneRule>().eq(SceneRule::getSceneId, sceneId)
        );
    }

    public List<SmartScene> listByStationId(Long stationId) {
        return smartSceneMapper.selectList(
            new LambdaQueryWrapper<SmartScene>().eq(SmartScene::getStationId, stationId)
        );
    }

    public void delete(Long id) {
        SmartScene scene = getById(id);
        smartSceneMapper.deleteById(id);
        sceneRuleMapper.delete(
            new LambdaQueryWrapper<SceneRule>().eq(SceneRule::getSceneId, id)
        );
        scenarioCacheService.evictStationScenes(scene.getStationId());
        scenarioCacheService.evictSceneRules(id);
        log.info("删除场景: {}", id);
    }
}
