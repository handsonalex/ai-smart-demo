package com.smart.infrastructure.cache;

import com.smart.common.constants.CacheExpire;
import com.smart.common.constants.RedisKeys;
import com.smart.domain.entity.DecisionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 决策结果缓存服务
 *
 * <p>为决策记录提供 Redis 缓存管理，主要用于：</p>
 * <ul>
 *   <li>加速决策结果的查询（避免每次都查数据库）</li>
 *   <li>为下游系统提供快速的决策状态查询入口</li>
 * </ul>
 *
 * <p>同样采用 Cache-Aside 模式：</p>
 * <ul>
 *   <li>决策完成后，将决策记录写入缓存（TTL 1 小时）</li>
 *   <li>查询时先查缓存，缓存未命中再查数据库（数据库查询由上层服务负责）</li>
 *   <li>决策状态变更时，通过 evict 方法删除缓存</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionCacheService {

    /** Redis 通用操作服务 */
    private final RedisService redisService;

    /**
     * 缓存决策记录
     *
     * <p>在决策完成后调用，将决策记录写入 Redis 缓存。
     * 缓存过期时间为 1 小时（{@link CacheExpire#ONE_HOUR}），
     * 过期后查询会触发重新从数据库加载。</p>
     *
     * @param record 决策记录实体
     */
    public void cacheDecision(DecisionRecord record) {
        String key = RedisKeys.DECISION_PREFIX + record.getId();
        redisService.set(key, record, CacheExpire.ONE_HOUR, TimeUnit.SECONDS);
    }

    /**
     * 从缓存中获取决策记录
     *
     * <p>如果缓存中不存在（返回 null），调用方应当回源查询数据库。</p>
     *
     * @param decisionId 决策记录 ID
     * @return 缓存的决策记录，缓存未命中时返回 null
     */
    public DecisionRecord getDecision(Long decisionId) {
        String key = RedisKeys.DECISION_PREFIX + decisionId;
        return redisService.get(key);
    }

    /**
     * 删除决策缓存
     *
     * <p>在决策状态发生变更（如执行完成、执行失败）时调用，
     * 确保下次查询能获取到最新的数据库数据。</p>
     *
     * @param decisionId 决策记录 ID
     */
    public void evictDecision(Long decisionId) {
        String key = RedisKeys.DECISION_PREFIX + decisionId;
        redisService.delete(key);
    }
}
