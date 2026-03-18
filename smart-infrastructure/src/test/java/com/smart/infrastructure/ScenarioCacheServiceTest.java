package com.smart.infrastructure;

import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.domain.mapper.SceneRuleMapper;
import com.smart.domain.mapper.SmartSceneMapper;
import com.smart.infrastructure.cache.RedisService;
import com.smart.infrastructure.cache.ScenarioCacheService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioCacheServiceTest {

    @Mock
    private RedisService redisService;
    @Mock
    private SmartSceneMapper smartSceneMapper;
    @Mock
    private SceneRuleMapper sceneRuleMapper;

    @InjectMocks
    private ScenarioCacheService scenarioCacheService;

    @Test
    @DisplayName("缓存命中时应直接返回场景列表")
    void getEnabledScenesFromCache() {
        SmartScene scene = new SmartScene();
        scene.setId(1L);
        scene.setSceneName("测试场景");
        List<SmartScene> cached = List.of(scene);

        when(redisService.<List<SmartScene>>get(anyString())).thenReturn(cached);

        List<SmartScene> result = scenarioCacheService.getEnabledScenes(1L);
        assertEquals(1, result.size());
        assertEquals("测试场景", result.get(0).getSceneName());
        verify(smartSceneMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("缓存未命中时应查询数据库并缓存")
    void getEnabledScenesFromDb() {
        when(redisService.<List<SmartScene>>get(anyString())).thenReturn(null);

        SmartScene scene = new SmartScene();
        scene.setId(1L);
        scene.setSceneName("DB场景");
        when(smartSceneMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(scene));

        List<SmartScene> result = scenarioCacheService.getEnabledScenes(1L);
        assertEquals(1, result.size());
        verify(redisService).set(anyString(), anyList(), anyLong(), any());
    }

    @Test
    @DisplayName("获取场景规则 - 缓存命中")
    void getSceneRulesFromCache() {
        SceneRule rule = new SceneRule();
        rule.setId(1L);
        List<SceneRule> cached = List.of(rule);

        when(redisService.<List<SceneRule>>get(anyString())).thenReturn(cached);

        List<SceneRule> result = scenarioCacheService.getSceneRules(1L);
        assertEquals(1, result.size());
        verify(sceneRuleMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("清除电站场景缓存")
    void evictStationScenes() {
        scenarioCacheService.evictStationScenes(1L);
        verify(redisService).delete(anyString());
    }

    @Test
    @DisplayName("清除场景规则缓存")
    void evictSceneRules() {
        scenarioCacheService.evictSceneRules(1L);
        verify(redisService).delete(anyString());
    }
}
