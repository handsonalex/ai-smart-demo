package com.smart.application.service;

import com.smart.common.enums.DecisionStage;
import com.smart.common.enums.DecisionStatus;
import com.smart.domain.entity.DecisionRecord;
import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.domain.mapper.DecisionRecordMapper;
import com.smart.infrastructure.cache.DecisionCacheService;
import com.smart.infrastructure.cache.ScenarioCacheService;
import com.smart.infrastructure.kafka.DecisionResultMessage;
import com.smart.infrastructure.kafka.DecisionResultProducer;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import com.smart.infrastructure.rag.RagPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 决策应用服务：核心 AI 决策流程
 *
 * 决策流程：
 * 1. 接收设备数据
 * 2. 匹配场景规则（RuleEngineService）
 * 3. RAG 检索相关知识
 * 4. 调用 AI 大模型推理
 * 5. 生成决策结果并下发
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionAppService {

    private final DecisionRecordMapper decisionRecordMapper;
    private final ScenarioCacheService scenarioCacheService;
    private final DecisionCacheService decisionCacheService;
    private final RuleEngineService ruleEngineService;
    private final RagPipelineService ragPipelineService;
    private final DecisionResultProducer decisionResultProducer;

    /**
     * 处理设备数据，触发决策流程
     */
    public void processDeviceData(DeviceDataMessage data) {
        log.info("开始决策流程, stationId: {}, deviceId: {}", data.getStationId(), data.getDeviceId());

        // 第一阶段：规则匹配
        List<SmartScene> scenes = scenarioCacheService.getEnabledScenes(data.getStationId());
        SmartScene matchedScene = null;
        for (SmartScene scene : scenes) {
            List<SceneRule> rules = scenarioCacheService.getSceneRules(scene.getId());
            if (ruleEngineService.match(rules, data)) {
                matchedScene = scene;
                break;
            }
        }

        if (matchedScene == null) {
            log.info("无匹配场景, stationId: {}", data.getStationId());
            return;
        }
        log.info("匹配到场景: {}, sceneId: {}", matchedScene.getSceneName(), matchedScene.getId());

        // 创建决策记录
        DecisionRecord record = new DecisionRecord();
        record.setStationId(data.getStationId());
        record.setSceneId(matchedScene.getId());
        record.setTriggerData(com.smart.common.utils.JsonUtil.toJson(data));
        record.setStatus(DecisionStatus.EXECUTING.getCode());
        record.setStage(DecisionStage.RULE_MATCH.getCode());
        decisionRecordMapper.insert(record);

        try {
            // 第二阶段：RAG 检索
            record.setStage(DecisionStage.RAG_RETRIEVAL.getCode());
            String query = buildRagQuery(matchedScene, data);
            List<String> knowledgeChunks = ragPipelineService.retrieve(query, 5);
            log.info("RAG 检索到 {} 个相关片段", knowledgeChunks.size());

            // 第三阶段：AI 推理
            record.setStage(DecisionStage.AI_INFERENCE.getCode());
            // TODO: 构建 Prompt 并调用 Spring AI ChatClient 进行推理
            String aiResponse = "TODO: AI 推理结果";
            record.setAiResponse(aiResponse);

            // 第四阶段：生成决策结果并下发
            record.setStage(DecisionStage.COMMAND_DISPATCH.getCode());
            record.setDecisionResult(aiResponse);
            record.setStatus(DecisionStatus.COMPLETED.getCode());
            record.setExecutedAt(LocalDateTime.now());
            decisionRecordMapper.updateById(record);

            // 发送决策结果到 Kafka
            DecisionResultMessage resultMessage = new DecisionResultMessage();
            resultMessage.setDecisionId(record.getId());
            resultMessage.setStationId(record.getStationId());
            resultMessage.setSceneId(record.getSceneId());
            resultMessage.setDecisionResult(record.getDecisionResult());
            resultMessage.setStatus(record.getStatus());
            resultMessage.setAiResponse(aiResponse);
            decisionResultProducer.send(resultMessage);

            // 缓存决策记录
            decisionCacheService.cacheDecision(record);
            log.info("决策流程完成, decisionId: {}", record.getId());

        } catch (Exception e) {
            log.error("决策流程异常, decisionId: {}", record.getId(), e);
            record.setStatus(DecisionStatus.FAILED.getCode());
            decisionRecordMapper.updateById(record);
        }
    }

    /**
     * 查询决策记录
     */
    public DecisionRecord getById(Long id) {
        DecisionRecord cached = decisionCacheService.getDecision(id);
        if (cached != null) {
            return cached;
        }
        return decisionRecordMapper.selectById(id);
    }

    private String buildRagQuery(SmartScene scene, DeviceDataMessage data) {
        return String.format("场景[%s] 设备功率:%.2f 温度:%.2f SOC:%.2f 最佳决策方案",
            scene.getSceneName(),
            data.getPower(),
            data.getTemperature(),
            data.getSoc());
    }
}
