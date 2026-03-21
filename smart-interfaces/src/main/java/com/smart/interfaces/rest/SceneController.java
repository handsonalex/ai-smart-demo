package com.smart.interfaces.rest;

import com.smart.application.service.SceneAppService;
import com.smart.common.result.Result;
import com.smart.domain.entity.SceneRule;
import com.smart.domain.entity.SmartScene;
import com.smart.interfaces.dto.request.SceneCreateReq;
import com.smart.interfaces.dto.response.SceneDetailResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景管理控制器 —— 接口层（Interfaces Layer）
 *
 * <p>提供智慧能源场景的 RESTful 接口。场景是决策引擎的核心概念，
 * 每个场景包含若干条规则（SceneRule），用于定义在何种条件下触发何种动作。
 * <ul>
 *   <li>POST   /api/v1/scenes                     — 创建场景（含规则列表）</li>
 *   <li>GET    /api/v1/scenes/{id}                — 查询场景详情（含规则列表）</li>
 *   <li>GET    /api/v1/scenes/station/{stationId} — 查询某电站下的所有场景</li>
 *   <li>DELETE /api/v1/scenes/{id}                — 删除场景</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>场景与规则是一对多的聚合关系，创建场景时可同时创建关联的规则列表</li>
 *   <li>查询场景详情时会同时返回其关联的规则列表，形成完整的聚合视图</li>
 *   <li>场景类型包括：削峰填谷、需量控制、光伏自消纳、应急备电等储能场景</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "场景管理")
@RestController
@RequestMapping("/api/v1/scenes")
@RequiredArgsConstructor
public class SceneController {

    /** 场景应用服务，负责场景与规则的业务编排 */
    private final SceneAppService sceneAppService;

    /**
     * 创建场景（含关联规则）
     *
     * <p>处理流程：
     * <ol>
     *   <li>参数校验（@Valid 触发 JSR 380 校验）</li>
     *   <li>将请求 DTO 转换为领域对象（场景 + 规则列表）</li>
     *   <li>调用应用服务创建场景及其规则</li>
     *   <li>将领域对象转换为响应 DTO 返回</li>
     * </ol>
     *
     * @param req 创建场景请求体，包含场景基本信息和规则列表
     * @return 创建成功的场景详情（含规则）
     */
    @Operation(summary = "创建场景")
    @PostMapping
    public Result<SceneDetailResp> create(@Valid @RequestBody SceneCreateReq req) {
        // 将场景请求 DTO 转换为场景领域实体
        SmartScene scene = new SmartScene();
        BeanUtils.copyProperties(req, scene);
        // 将规则 DTO 列表逐个转换为规则领域实体
        List<SceneRule> rules = new ArrayList<>();
        if (req.getRules() != null) {
            for (SceneCreateReq.RuleItem item : req.getRules()) {
                SceneRule rule = new SceneRule();
                BeanUtils.copyProperties(item, rule);
                rules.add(rule);
            }
        }
        // 调用应用服务，在同一事务中创建场景和关联规则
        SmartScene created = sceneAppService.create(scene, rules);
        return Result.success(toDetailResp(created, rules));
    }

    /**
     * 查询场景详情（含关联规则）
     *
     * <p>处理流程：路径参数获取 ID → 分别查询场景和规则 → 组装为完整的响应 DTO
     *
     * @param id 场景主键 ID
     * @return 场景详情，包含完整的规则列表
     */
    @Operation(summary = "查询场景详情")
    @GetMapping("/{id}")
    public Result<SceneDetailResp> getById(@PathVariable Long id) {
        SmartScene scene = sceneAppService.getById(id);
        // 单独查询该场景关联的规则列表
        List<SceneRule> rules = sceneAppService.getRulesBySceneId(id);
        return Result.success(toDetailResp(scene, rules));
    }

    /**
     * 查询电站下的场景列表
     *
     * <p>处理流程：路径参数获取电站 ID → 查询场景列表 → 逐个加载关联规则 → 转换为响应 DTO
     * <p>注意：此处对每个场景都会查询一次规则列表，存在 N+1 查询问题，
     * 在大数据量场景下可考虑优化为批量查询
     *
     * @param stationId 电站 ID
     * @return 该电站下的所有场景详情列表
     */
    @Operation(summary = "查询电站下场景列表")
    @GetMapping("/station/{stationId}")
    public Result<List<SceneDetailResp>> listByStation(@PathVariable Long stationId) {
        List<SmartScene> scenes = sceneAppService.listByStationId(stationId);
        List<SceneDetailResp> respList = scenes.stream()
            .map(s -> toDetailResp(s, sceneAppService.getRulesBySceneId(s.getId())))
            .toList();
        return Result.success(respList);
    }

    /**
     * 删除场景
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务执行删除（通常会级联删除关联规则）
     *
     * @param id 要删除的场景 ID
     * @return 空响应，表示删除成功
     */
    @Operation(summary = "删除场景")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sceneAppService.delete(id);
        return Result.success();
    }

    /**
     * 将场景领域实体和规则列表转换为场景详情响应 DTO
     *
     * <p>该方法负责将聚合根（SmartScene）及其子实体（SceneRule 列表）
     * 组装为一个完整的响应对象，供前端展示
     *
     * @param scene 场景领域实体
     * @param rules 该场景关联的规则列表
     * @return 场景详情响应 DTO
     */
    private SceneDetailResp toDetailResp(SmartScene scene, List<SceneRule> rules) {
        SceneDetailResp resp = new SceneDetailResp();
        BeanUtils.copyProperties(scene, resp);
        // 将规则领域实体列表转换为规则响应 DTO 列表
        if (rules != null) {
            List<SceneDetailResp.RuleResp> ruleResps = rules.stream().map(r -> {
                SceneDetailResp.RuleResp ruleResp = new SceneDetailResp.RuleResp();
                BeanUtils.copyProperties(r, ruleResp);
                return ruleResp;
            }).toList();
            resp.setRules(ruleResps);
        }
        return resp;
    }
}
