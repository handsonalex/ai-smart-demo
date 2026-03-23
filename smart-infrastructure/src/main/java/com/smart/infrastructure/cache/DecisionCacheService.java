package com.smart.infrastructure.cache;

import com.smart.domain.entity.DecisionRecord;

/**
 * 决策结果缓存服务接口（Cache-Aside 模式）
 *
 * @author Joseph Ho
 */
public interface DecisionCacheService {

    void cacheDecision(DecisionRecord record);

    DecisionRecord getDecision(Long decisionId);

    void evictDecision(Long decisionId);
}
