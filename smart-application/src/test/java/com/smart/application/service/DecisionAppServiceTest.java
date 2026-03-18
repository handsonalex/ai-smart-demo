package com.smart.application.service;

import com.smart.domain.entity.DecisionRecord;
import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.domain.mapper.DecisionRecordMapper;
import com.smart.infrastructure.cache.DecisionCacheService;
import com.smart.infrastructure.cache.ScenarioCacheService;
import com.smart.infrastructure.kafka.DecisionResultProducer;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import com.smart.infrastructure.rag.RagPipelineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionAppServiceTest {

    @Mock
    private DecisionRecordMapper decisionRecordMapper;
    @Mock
    private ScenarioCacheService scenarioCacheService;
    @Mock
    private DecisionCacheService decisionCacheService;
    @Mock
    private RuleEngineService ruleEngineService;
    @Mock
    private RagPipelineService ragPipelineService;
    @Mock
    private DecisionResultProducer decisionResultProducer;

    @InjectMocks
    private DecisionAppService decisionAppService;

    @Test
    @DisplayName("无匹配场景时不创建决策记录")
    void processDeviceDataNoMatch() {
        DeviceDataMessage data = buildData();
        when(scenarioCacheService.getEnabledScenes(1L)).thenReturn(Collections.emptyList());

        decisionAppService.processDeviceData(data);

        verify(decisionRecordMapper, never()).insert(any());
    }

    @Test
    @DisplayName("匹配场景时应创建决策记录并完成流程")
    void processDeviceDataWithMatch() {
        DeviceDataMessage data = buildData();
        SmartScene scene = new SmartScene();
        scene.setId(10L);
        scene.setSceneName("测试场景");
        scene.setStationId(1L);

        SceneRule rule = new SceneRule();
        rule.setId(1L);

        when(scenarioCacheService.getEnabledScenes(1L)).thenReturn(List.of(scene));
        when(scenarioCacheService.getSceneRules(10L)).thenReturn(List.of(rule));
        when(ruleEngineService.match(anyList(), any())).thenReturn(true);
        when(ragPipelineService.retrieve(anyString(), anyInt())).thenReturn(List.of("知识片段1"));

        decisionAppService.processDeviceData(data);

        verify(decisionRecordMapper).insert(any(DecisionRecord.class));
        verify(decisionRecordMapper).updateById(any(DecisionRecord.class));
        verify(decisionResultProducer).send(any());
        verify(decisionCacheService).cacheDecision(any());
    }

    @Test
    @DisplayName("查询决策 - 缓存命中")
    void getByIdFromCache() {
        DecisionRecord record = new DecisionRecord();
        record.setId(1L);
        when(decisionCacheService.getDecision(1L)).thenReturn(record);

        DecisionRecord result = decisionAppService.getById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(decisionRecordMapper, never()).selectById(any());
    }

    @Test
    @DisplayName("查询决策 - 缓存未命中走数据库")
    void getByIdFromDb() {
        when(decisionCacheService.getDecision(1L)).thenReturn(null);
        DecisionRecord record = new DecisionRecord();
        record.setId(1L);
        when(decisionRecordMapper.selectById(1L)).thenReturn(record);

        DecisionRecord result = decisionAppService.getById(1L);
        assertNotNull(result);
        verify(decisionRecordMapper).selectById(1L);
    }

    private DeviceDataMessage buildData() {
        DeviceDataMessage data = new DeviceDataMessage();
        data.setDeviceId(1L);
        data.setStationId(1L);
        data.setPower(new BigDecimal("85.5"));
        data.setVoltage(new BigDecimal("380"));
        data.setCurrent(new BigDecimal("12.5"));
        data.setTemperature(new BigDecimal("35"));
        data.setSoc(new BigDecimal("82"));
        return data;
    }
}
