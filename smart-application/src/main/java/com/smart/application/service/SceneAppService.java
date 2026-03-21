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

/**
 * 场景应用服务 —— 管理智能场景（SmartScene）及其关联规则（SceneRule）的生命周期
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层（应用服务层）</b>，
 * 负责编排场景和规则的创建、查询、删除等业务用例。</p>
 *
 * <h3>核心设计要点</h3>
 * <ul>
 *   <li><b>事务管理</b>：场景和规则的创建/删除在同一个数据库事务中完成，保证数据一致性</li>
 *   <li><b>缓存一致性</b>：每次写操作后主动清除 Redis 缓存（Cache Eviction），
 *       确保 {@link DecisionAppService} 在决策流程中读取到最新的场景和规则数据。
 *       采用"写时失效"策略而非"写时更新"，简化缓存逻辑并避免并发写入导致的缓存脏数据。</li>
 * </ul>
 *
 * <h3>场景与规则的关系</h3>
 * <p>一个场景（SmartScene）可以关联多条规则（SceneRule），规则之间是 AND 逻辑关系。
 * 当设备数据满足所有规则时，该场景被触发，进入 AI 决策流程。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SceneAppService {

    /** 智能场景 Mapper */
    private final SmartSceneMapper smartSceneMapper;

    /** 场景规则 Mapper */
    private final SceneRuleMapper sceneRuleMapper;

    /** 场景缓存服务，用于在写操作后清除相关缓存，保证决策流程读取数据的新鲜度 */
    private final ScenarioCacheService scenarioCacheService;

    /**
     * 创建智能场景及其关联规则（事务性操作）
     *
     * <p><b>事务设计意图：</b>使用 @Transactional 保证场景和规则在同一个事务中写入数据库。
     * 如果规则插入过程中出现异常，场景的插入也会被回滚，避免出现"有场景无规则"的脏数据。
     * rollbackFor=Exception.class 表示所有异常（包括受检异常）都触发回滚。</p>
     *
     * <p><b>缓存清除设计意图：</b>场景创建成功后，主动清除该电站下的场景列表缓存。
     * 这样下次决策流程查询场景时会重新从数据库加载并写入缓存，确保新场景能被及时匹配到。
     * 采用"失效"而非"更新"策略，避免在高并发写入时出现缓存与数据库不一致的问题。</p>
     *
     * @param scene 智能场景实体
     * @param rules 关联的规则列表（可为 null，表示暂不配置规则）
     * @return 创建成功的场景实体（含数据库自动生成的 ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public SmartScene create(SmartScene scene, List<SceneRule> rules) {
        // 先插入场景主表，MyBatis-Plus 会自动回填生成的主键 ID
        smartSceneMapper.insert(scene);
        // 再逐条插入关联规则，设置规则与场景的关联关系（sceneId）
        if (rules != null) {
            for (SceneRule rule : rules) {
                rule.setSceneId(scene.getId());
                sceneRuleMapper.insert(rule);
            }
        }
        // 清除该电站的场景列表缓存，确保决策流程能读取到最新数据
        scenarioCacheService.evictStationScenes(scene.getStationId());
        log.info("创建场景: {}, 规则数: {}", scene.getId(), rules != null ? rules.size() : 0);
        return scene;
    }

    /**
     * 根据 ID 查询智能场景
     *
     * @param id 场景ID
     * @return 智能场景实体
     * @throws BizException 当场景不存在时抛出 SCENE_NOT_FOUND 业务异常
     */
    public SmartScene getById(Long id) {
        SmartScene scene = smartSceneMapper.selectById(id);
        if (scene == null) {
            throw new BizException(ErrorCode.SCENE_NOT_FOUND);
        }
        return scene;
    }

    /**
     * 根据场景 ID 查询其关联的所有规则
     *
     * @param sceneId 场景ID
     * @return 该场景下的规则列表
     */
    public List<SceneRule> getRulesBySceneId(Long sceneId) {
        return sceneRuleMapper.selectList(
            new LambdaQueryWrapper<SceneRule>().eq(SceneRule::getSceneId, sceneId)
        );
    }

    /**
     * 根据电站 ID 查询其下所有智能场景
     *
     * @param stationId 电站ID
     * @return 该电站下的场景列表
     */
    public List<SmartScene> listByStationId(Long stationId) {
        return smartSceneMapper.selectList(
            new LambdaQueryWrapper<SmartScene>().eq(SmartScene::getStationId, stationId)
        );
    }

    /**
     * 删除智能场景及其关联规则
     *
     * <p>删除流程：先查询场景（获取 stationId 用于缓存清除）→ 删除场景 → 删除关联规则 → 清除缓存。
     * 同时清除两个维度的缓存：电站维度的场景列表缓存、场景维度的规则列表缓存。</p>
     *
     * @param id 场景ID
     * @throws BizException 当场景不存在时抛出业务异常（由 getById 保证）
     */
    public void delete(Long id) {
        // 先查询场景实体，获取 stationId 用于后续缓存清除，同时验证场景是否存在
        SmartScene scene = getById(id);
        // 删除场景主表记录
        smartSceneMapper.deleteById(id);
        // 级联删除该场景下的所有规则
        sceneRuleMapper.delete(
            new LambdaQueryWrapper<SceneRule>().eq(SceneRule::getSceneId, id)
        );
        // 清除该电站的场景列表缓存
        scenarioCacheService.evictStationScenes(scene.getStationId());
        // 清除该场景的规则列表缓存
        scenarioCacheService.evictSceneRules(id);
        log.info("删除场景: {}", id);
    }
}
