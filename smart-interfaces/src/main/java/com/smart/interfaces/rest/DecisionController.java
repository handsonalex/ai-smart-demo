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
 * 决策管理控制器 —— 接口层（Interfaces Layer）
 *
 * <p>提供决策记录的查询接口。决策记录由系统自动生成（当设备上报数据触发场景规则时），
 * 不支持手动创建，因此本 Controller 只提供查询类接口：
 * <ul>
 *   <li>GET /api/v1/decisions/{id}      — 查询决策详情</li>
 *   <li>GET /api/v1/decisions/{id}/logs — 查询决策的执行日志</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>决策记录是只读资源，由 Kafka 消费者触发决策引擎自动创建</li>
 *   <li>决策日志记录了决策执行过程中每个阶段的输入输出和耗时，便于问题排查和性能分析</li>
 *   <li>日志通过独立的 {@link DecisionLogService} 查询，与决策服务解耦</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "决策管理")
@RestController
@RequestMapping("/api/v1/decisions")
@RequiredArgsConstructor
public class DecisionController {

    /** 决策应用服务，负责决策记录的查询 */
    private final DecisionAppService decisionAppService;

    /** 决策日志服务，负责查询决策执行过程的详细日志（基础设施层） */
    private final DecisionLogService decisionLogService;

    /**
     * 查询决策详情
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务查询决策记录 → 领域对象转响应 DTO
     * <p>如果决策记录不存在，返回空的响应对象（而非抛出异常），属于宽松处理策略
     *
     * @param id 决策记录主键 ID
     * @return 决策详情信息，包含触发数据、决策结果、AI 响应等
     */
    @Operation(summary = "查询决策详情")
    @GetMapping("/{id}")
    public Result<DecisionDetailResp> getById(@PathVariable Long id) {
        DecisionRecord record = decisionAppService.getById(id);
        DecisionDetailResp resp = new DecisionDetailResp();
        // 仅在记录存在时进行属性拷贝，避免空指针异常
        if (record != null) {
            BeanUtils.copyProperties(record, resp);
        }
        return Result.success(resp);
    }

    /**
     * 查询决策执行日志
     *
     * <p>处理流程：路径参数获取决策 ID → 调用日志服务查询 → 批量转换为响应 DTO
     * <p>决策日志记录了决策流程每个阶段（如规则匹配、AI推理、指令下发）的详细信息，
     * 包括每个阶段的输入、输出、耗时和成功状态
     *
     * @param id 决策记录 ID
     * @return 该决策对应的所有执行日志列表
     */
    @Operation(summary = "查询决策日志")
    @GetMapping("/{id}/logs")
    public Result<List<DecisionLogResp>> getLogs(@PathVariable Long id) {
        List<DecisionLog> logs = decisionLogService.queryByDecisionId(id);
        // 将日志领域对象列表转换为响应 DTO 列表
        List<DecisionLogResp> respList = logs.stream().map(log -> {
            DecisionLogResp resp = new DecisionLogResp();
            BeanUtils.copyProperties(log, resp);
            return resp;
        }).toList();
        return Result.success(respList);
    }
}
