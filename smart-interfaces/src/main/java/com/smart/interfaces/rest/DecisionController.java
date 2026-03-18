package com.smart.interfaces.rest;

import com.smart.application.service.DecisionAppService;
import com.smart.common.result.Result;
import com.smart.domain.entity.DecisionRecord;
import com.smart.infrastructure.log.DecisionLog;
import com.smart.infrastructure.log.DecisionLogService;
import com.smart.interfaces.dto.response.DecisionDetailResp;
import com.smart.interfaces.dto.response.DecisionLogResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 决策管理控制器
 *
 * @author Joseph Ho
 */
@Tag(name = "决策管理")
@RestController
@RequestMapping("/api/v1/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionAppService decisionAppService;
    private final DecisionLogService decisionLogService;

    @Operation(summary = "查询决策详情")
    @GetMapping("/{id}")
    public Result<DecisionDetailResp> getById(@PathVariable Long id) {
        DecisionRecord record = decisionAppService.getById(id);
        DecisionDetailResp resp = new DecisionDetailResp();
        if (record != null) {
            BeanUtils.copyProperties(record, resp);
        }
        return Result.success(resp);
    }

    @Operation(summary = "查询决策日志")
    @GetMapping("/{id}/logs")
    public Result<List<DecisionLogResp>> getLogs(@PathVariable Long id) {
        List<DecisionLog> logs = decisionLogService.queryByDecisionId(id);
        List<DecisionLogResp> respList = logs.stream().map(log -> {
            DecisionLogResp resp = new DecisionLogResp();
            BeanUtils.copyProperties(log, resp);
            return resp;
        }).toList();
        return Result.success(respList);
    }
}
