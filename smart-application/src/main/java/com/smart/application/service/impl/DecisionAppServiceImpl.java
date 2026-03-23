package com.smart.application.service.impl;

import com.smart.application.service.DecisionAppService;
import com.smart.application.service.RuleEngineService;
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
 * 决策应用服务 —— 整个 AI 智能决策系统的核心编排层
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层（应用服务层）</b>，
 * 负责编排领域对象、基础设施组件，完成一次完整的业务用例。
 * 它本身不包含业务规则，而是将规则引擎、RAG 检索、AI 推理、消息下发等能力串联起来。</p>
 *
 * <h3>核心职责：AI 决策流程的四阶段编排</h3>
 * <ol>
 *   <li><b>规则匹配（Rule Match）</b> —— 从缓存获取电站下已启用的场景，逐一用 RuleEngineService 进行条件匹配，找到第一个命中的场景</li>
 *   <li><b>RAG 检索（RAG Retrieval）</b> —— 根据命中场景和设备数据构建查询语句，从向量数据库中检索相关知识片段，为 AI 推理提供上下文</li>
 *   <li><b>AI 推理（AI Inference）</b> —— 将场景信息、设备数据、知识片段组装为 Prompt，调用大模型生成决策建议（当前为 TODO 占位）</li>
 *   <li><b>指令下发（Command Dispatch）</b> —— 将决策结果通过 Kafka 消息下发给设备控制层，同时缓存决策记录以供后续查询</li>
 * </ol>
 *
 * <h3>数据流向</h3>
 * <pre>
 * Kafka(设备数据) → processDeviceData() → 规则匹配 → RAG检索 → AI推理 → Kafka(决策结果)
 *                                                                         ↓
 *                                                                    DB + Redis 缓存
 * </pre>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionAppServiceImpl implements DecisionAppService {

    /** 决策记录 Mapper，用于持久化决策的各阶段状态及最终结果 */
    private final DecisionRecordMapper decisionRecordMapper;

    /** 场景缓存服务，提供「电站→已启用场景」和「场景→规则列表」的 Redis 缓存读取能力，避免每次决策都查库 */
    private final ScenarioCacheService scenarioCacheService;

    /** 决策缓存服务，用于将已完成的决策记录写入 Redis，加速后续查询 */
    private final DecisionCacheService decisionCacheService;

    /** 规则引擎服务，负责判断设备上报的数据是否满足某个场景的所有规则条件（AND 逻辑） */
    private final RuleEngineService ruleEngineService;

    /** RAG 管道服务，负责根据查询语句从向量数据库中检索最相关的知识文本片段 */
    private final RagPipelineService ragPipelineService;

    /** Kafka 决策结果生产者，负责将最终决策结果发送到下游消费者（如设备控制服务） */
    private final DecisionResultProducer decisionResultProducer;

    /**
     * 处理设备上报数据，触发完整的 AI 决策流程
     *
     * <p>本方法是决策流程的入口，由 Kafka 消费者监听设备数据消息后调用。
     * 内部按「规则匹配 → RAG 检索 → AI 推理 → 指令下发」四个阶段顺序执行，
     * 每个阶段都会更新决策记录的 stage 字段，便于追踪决策进度和定位失败环节。</p>
     *
     * <p><b>设计要点：</b></p>
     * <ul>
     *   <li>规则匹配阶段采用"首次命中即停止"策略（First-Match），一个设备数据只会触发一个场景的决策</li>
     *   <li>决策记录在流程开始时就持久化（status=EXECUTING），确保即使后续阶段失败也有据可查</li>
     *   <li>异常处理：任一阶段失败，将决策状态标记为 FAILED 并更新到数据库，不会影响其他设备数据的处理</li>
     * </ul>
     *
     * @param data 设备上报的数据消息，包含电站ID、设备ID、功率、温度、SOC 等指标
     */
    @Override
    public void processDeviceData(DeviceDataMessage data) {
        log.info("开始决策流程, stationId: {}, deviceId: {}", data.getStationId(), data.getDeviceId());

        // ======================== 第一阶段：规则匹配（Rule Match） ========================
        // 从 Redis 缓存中获取该电站下所有已启用的智能场景
        List<SmartScene> scenes = scenarioCacheService.getEnabledScenes(data.getStationId());
        SmartScene matchedScene = null;
        // 遍历每个场景，获取其关联的规则列表，使用规则引擎进行匹配
        // 采用 First-Match 策略：一旦找到第一个匹配的场景就停止遍历，避免重复决策
        for (SmartScene scene : scenes) {
            List<SceneRule> rules = scenarioCacheService.getSceneRules(scene.getId());
            if (ruleEngineService.match(rules, data)) {
                matchedScene = scene;
                break;
            }
        }

        // 若没有任何场景匹配，说明当前设备数据属于正常范围，无需触发 AI 决策，直接返回
        if (matchedScene == null) {
            log.info("无匹配场景, stationId: {}", data.getStationId());
            return;
        }
        log.info("匹配到场景: {}, sceneId: {}", matchedScene.getSceneName(), matchedScene.getId());

        // 创建决策记录并持久化，初始状态为 EXECUTING，阶段为 RULE_MATCH
        // 提前入库的目的：即使后续流程异常中断，也能在数据库中追溯到这条决策的触发信息
        DecisionRecord record = new DecisionRecord();
        record.setStationId(data.getStationId());
        record.setSceneId(matchedScene.getId());
        record.setTriggerData(com.smart.common.utils.JsonUtil.toJson(data));
        record.setStatus(DecisionStatus.EXECUTING.getCode());
        record.setStage(DecisionStage.RULE_MATCH.getCode());
        decisionRecordMapper.insert(record);

        try {
            // ======================== 第二阶段：RAG 检索（RAG Retrieval） ========================
            // 更新决策阶段标识，便于监控和问题排查
            record.setStage(DecisionStage.RAG_RETRIEVAL.getCode());
            // 根据匹配到的场景和设备数据，构建自然语言查询语句
            String query = buildRagQuery(matchedScene, data);
            // 调用 RAG 管道从向量数据库中检索 Top-5 最相关的知识文本片段
            // 这些知识片段将作为 AI 推理的上下文，帮助大模型做出更准确的决策
            List<String> knowledgeChunks = ragPipelineService.retrieve(query, 5);
            log.info("RAG 检索到 {} 个相关片段", knowledgeChunks.size());

            // ======================== 第三阶段：AI 推理（AI Inference） ========================
            record.setStage(DecisionStage.AI_INFERENCE.getCode());
            // TODO: 构建 Prompt（包含场景描述 + 设备数据 + RAG 知识片段）并调用 Spring AI ChatClient 进行推理
            // 预期：将 knowledgeChunks 拼接为上下文，与设备指标一起发送给大模型，由大模型输出决策建议
            String aiResponse = "TODO: AI 推理结果";
            record.setAiResponse(aiResponse);

            // ======================== 第四阶段：指令下发（Command Dispatch） ========================
            // 将 AI 推理结果作为最终决策，更新决策记录状态为 COMPLETED
            record.setStage(DecisionStage.COMMAND_DISPATCH.getCode());
            record.setDecisionResult(aiResponse);
            record.setStatus(DecisionStatus.COMPLETED.getCode());
            record.setExecutedAt(LocalDateTime.now());
            decisionRecordMapper.updateById(record);

            // 构建 Kafka 消息体，将决策结果发送到下游（如设备控制服务、前端推送服务等）
            DecisionResultMessage resultMessage = new DecisionResultMessage();
            resultMessage.setDecisionId(record.getId());
            resultMessage.setStationId(record.getStationId());
            resultMessage.setSceneId(record.getSceneId());
            resultMessage.setDecisionResult(record.getDecisionResult());
            resultMessage.setStatus(record.getStatus());
            resultMessage.setAiResponse(aiResponse);
            decisionResultProducer.send(resultMessage);

            // 将完成的决策记录写入 Redis 缓存，后续查询可直接走缓存，减轻数据库压力
            decisionCacheService.cacheDecision(record);
            log.info("决策流程完成, decisionId: {}", record.getId());

        } catch (Exception e) {
            // 任一阶段发生异常，将决策状态标记为 FAILED 并持久化，便于后续排查和重试
            log.error("决策流程异常, decisionId: {}", record.getId(), e);
            record.setStatus(DecisionStatus.FAILED.getCode());
            decisionRecordMapper.updateById(record);
        }
    }

    /**
     * 根据决策ID查询决策记录
     *
     * <p>优先从 Redis 缓存中获取，缓存未命中时再查询数据库。
     * 这是典型的 Cache-Aside（旁路缓存）模式，兼顾查询性能与数据一致性。</p>
     *
     * @param id 决策记录ID
     * @return 决策记录实体，包含触发数据、AI 响应、决策结果等完整信息
     */
    @Override
    public DecisionRecord getById(Long id) {
        // 先查 Redis 缓存
        DecisionRecord cached = decisionCacheService.getDecision(id);
        if (cached != null) {
            return cached;
        }
        // 缓存未命中，回源查询数据库
        return decisionRecordMapper.selectById(id);
    }

    /**
     * 构建 RAG 检索查询语句
     *
     * <p>将场景名称和设备关键指标拼接为自然语言查询，
     * 用于在向量数据库中做语义相似度检索，召回最相关的知识文档片段。</p>
     *
     * @param scene 匹配到的智能场景
     * @param data  设备上报数据
     * @return 用于 RAG 检索的自然语言查询字符串
     */
    private String buildRagQuery(SmartScene scene, DeviceDataMessage data) {
        return String.format("场景[%s] 设备功率:%.2f 温度:%.2f SOC:%.2f 最佳决策方案",
            scene.getSceneName(),
            data.getPower(),
            data.getTemperature(),
            data.getSoc());
    }
}
