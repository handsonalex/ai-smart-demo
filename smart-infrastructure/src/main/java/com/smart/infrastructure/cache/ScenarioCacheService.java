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
 * 场景缓存服务 —— Cache-Aside（旁路缓存）模式实现
 *
 * <p>本类为智能场景数据提供缓存管理，采用经典的 <b>Cache-Aside</b>（旁路缓存）模式：</p>
 * <ul>
 *   <li><b>读取流程</b>：先查缓存 -> 缓存命中则直接返回 -> 缓存未命中则查数据库 -> 将结果写入缓存 -> 返回</li>
 *   <li><b>更新流程</b>：先更新数据库 -> 再删除缓存（由 evict 方法提供）</li>
 * </ul>
 *
 * <p>Cache-Aside 模式的优势：</p>
 * <ul>
 *   <li>实现简单，是最常用的缓存策略</li>
 *   <li>缓存与数据库的一致性由「先写库再删缓存」保证（允许短暂的不一致窗口）</li>
 *   <li>缓存设置了过期时间（10 分钟），即使删除缓存失败，也能通过过期自动兜底</li>
 * </ul>
 *
 * <p>缓存的数据包括：</p>
 * <ul>
 *   <li>电站下所有启用的智能场景列表（按优先级排序）</li>
 *   <li>场景关联的规则列表</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioCacheService {

    /** Redis 通用操作服务 */
    private final RedisService redisService;

    /** 智能场景 Mapper（MySQL 数据源） */
    private final SmartSceneMapper smartSceneMapper;

    /** 场景规则 Mapper（MySQL 数据源） */
    private final SceneRuleMapper sceneRuleMapper;

    /**
     * 获取电站下所有启用的场景（Cache-Aside 读取模式）
     *
     * <p>读取流程：</p>
     * <ol>
     *   <li>构造缓存 key（前缀 + 电站 ID）</li>
     *   <li>查询 Redis 缓存，命中则直接返回</li>
     *   <li>缓存未命中，查询 MySQL 数据库（筛选条件：指定电站 + 已启用，按优先级升序排列）</li>
     *   <li>将查询结果写入 Redis 并设置过期时间（10 分钟），避免缓存长期不一致</li>
     * </ol>
     *
     * @param stationId 电站 ID
     * @return 该电站下所有启用的场景列表（按优先级升序排列）
     */
    public List<SmartScene> getEnabledScenes(Long stationId) {
        // 构造缓存 key
        String key = RedisKeys.SCENARIO_PREFIX + stationId;
        // 第一步：查询缓存
        List<SmartScene> scenes = redisService.get(key);
        if (scenes != null) {
            return scenes;
        }
        // 第二步：缓存未命中，从数据库查询
        scenes = smartSceneMapper.selectList(
            new LambdaQueryWrapper<SmartScene>()
                .eq(SmartScene::getStationId, stationId)
                .eq(SmartScene::getEnabled, true)
                .orderByAsc(SmartScene::getPriority)
        );
        if (scenes == null) {
            scenes = Collections.emptyList();
        }
        // 第三步：将结果写入缓存，设置 10 分钟过期时间
        redisService.set(key, scenes, CacheExpire.TEN_MINUTES, TimeUnit.SECONDS);
        return scenes;
    }

    /**
     * 获取场景下的所有规则（Cache-Aside 读取模式）
     *
     * <p>与 getEnabledScenes 相同的缓存策略，缓存场景关联的规则列表。</p>
     *
     * @param sceneId 场景 ID
     * @return 该场景关联的所有规则列表
     */
    public List<SceneRule> getSceneRules(Long sceneId) {
        // 构造缓存 key
        String key = RedisKeys.SCENARIO_PREFIX + "rules:" + sceneId;
        // 第一步：查询缓存
        List<SceneRule> rules = redisService.get(key);
        if (rules != null) {
            return rules;
        }
        // 第二步：缓存未命中，从数据库查询
        rules = sceneRuleMapper.selectList(
            new LambdaQueryWrapper<SceneRule>()
                .eq(SceneRule::getSceneId, sceneId)
        );
        if (rules == null) {
            rules = Collections.emptyList();
        }
        // 第三步：将结果写入缓存，设置 10 分钟过期时间
        redisService.set(key, rules, CacheExpire.TEN_MINUTES, TimeUnit.SECONDS);
        return rules;
    }

    /**
     * 清除电站场景缓存（Cache-Aside 失效策略）
     *
     * <p>当电站的场景配置发生变更时（新增、修改、删除场景），
     * 应在更新数据库后调用此方法删除缓存，下次读取时会自动从数据库重新加载。</p>
     *
     * @param stationId 电站 ID
     */
    public void evictStationScenes(Long stationId) {
        String key = RedisKeys.SCENARIO_PREFIX + stationId;
        redisService.delete(key);
        log.info("清除电站场景缓存, stationId: {}", stationId);
    }

    /**
     * 清除场景规则缓存（Cache-Aside 失效策略）
     *
     * <p>当场景的规则配置发生变更时，应在更新数据库后调用此方法删除缓存。</p>
     *
     * @param sceneId 场景 ID
     */
    public void evictSceneRules(Long sceneId) {
        String key = RedisKeys.SCENARIO_PREFIX + "rules:" + sceneId;
        redisService.delete(key);
        log.info("清除场景规则缓存, sceneId: {}", sceneId);
    }
}
