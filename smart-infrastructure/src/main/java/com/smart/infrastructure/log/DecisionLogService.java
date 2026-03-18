package com.smart.infrastructure.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 决策日志服务（Elasticsearch）
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionLogService {

    /**
     * 保存决策日志
     */
    public void save(DecisionLog decisionLog) {
        // TODO: 保存到 Elasticsearch
        log.info("保存决策日志, decisionId: {}, stage: {}", decisionLog.getDecisionId(), decisionLog.getStage());
    }

    /**
     * 查询决策日志
     */
    public List<DecisionLog> queryByDecisionId(Long decisionId) {
        // TODO: 从 Elasticsearch 查询
        log.info("查询决策日志, decisionId: {}", decisionId);
        return List.of();
    }

    /**
     * 按电站查询决策日志
     */
    public List<DecisionLog> queryByStationId(Long stationId, int page, int size) {
        // TODO: 从 Elasticsearch 分页查询
        log.info("查询电站决策日志, stationId: {}, page: {}, size: {}", stationId, page, size);
        return List.of();
    }
}
