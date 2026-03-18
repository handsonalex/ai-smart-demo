package com.smart.infrastructure.cache;

import com.smart.common.constants.CacheExpire;
import com.smart.common.constants.RedisKeys;
import com.smart.domain.entity.DecisionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 决策缓存服务
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionCacheService {

    private final RedisService redisService;

    /**
     * 缓存决策记录
     */
    public void cacheDecision(DecisionRecord record) {
        String key = RedisKeys.DECISION_PREFIX + record.getId();
        redisService.set(key, record, CacheExpire.ONE_HOUR, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的决策记录
     */
    public DecisionRecord getDecision(Long decisionId) {
        String key = RedisKeys.DECISION_PREFIX + decisionId;
        return redisService.get(key);
    }

    /**
     * 删除决策缓存
     */
    public void evictDecision(Long decisionId) {
        String key = RedisKeys.DECISION_PREFIX + decisionId;
        redisService.delete(key);
    }
}
