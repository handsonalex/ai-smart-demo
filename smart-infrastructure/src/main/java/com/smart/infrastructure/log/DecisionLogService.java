package com.smart.infrastructure.log;

import java.util.List;

/**
 * 决策日志服务接口 —— Elasticsearch 操作封装
 *
 * @author Joseph Ho
 */
public interface DecisionLogService {

    void save(DecisionLog decisionLog);

    List<DecisionLog> queryByDecisionId(Long decisionId);

    List<DecisionLog> queryByStationId(Long stationId, int page, int size);
}
