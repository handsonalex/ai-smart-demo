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

@Tag(name = "场景管理")
@RestController
@RequestMapping("/api/v1/scenes")
@RequiredArgsConstructor
public class SceneController {

    private final SceneAppService sceneAppService;

    @Operation(summary = "创建场景")
    @PostMapping
    public Result<SceneDetailResp> create(@Valid @RequestBody SceneCreateReq req) {
        SmartScene scene = new SmartScene();
        BeanUtils.copyProperties(req, scene);
        List<SceneRule> rules = new ArrayList<>();
        if (req.getRules() != null) {
            for (SceneCreateReq.RuleItem item : req.getRules()) {
                SceneRule rule = new SceneRule();
                BeanUtils.copyProperties(item, rule);
                rules.add(rule);
            }
        }
        SmartScene created = sceneAppService.create(scene, rules);
        return Result.success(toDetailResp(created, rules));
    }

    @Operation(summary = "查询场景详情")
    @GetMapping("/{id}")
    public Result<SceneDetailResp> getById(@PathVariable Long id) {
        SmartScene scene = sceneAppService.getById(id);
        List<SceneRule> rules = sceneAppService.getRulesBySceneId(id);
        return Result.success(toDetailResp(scene, rules));
    }

    @Operation(summary = "查询电站下场景列表")
    @GetMapping("/station/{stationId}")
    public Result<List<SceneDetailResp>> listByStation(@PathVariable Long stationId) {
        List<SmartScene> scenes = sceneAppService.listByStationId(stationId);
        List<SceneDetailResp> respList = scenes.stream()
            .map(s -> toDetailResp(s, sceneAppService.getRulesBySceneId(s.getId())))
            .toList();
        return Result.success(respList);
    }

    @Operation(summary = "删除场景")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sceneAppService.delete(id);
        return Result.success();
    }

    private SceneDetailResp toDetailResp(SmartScene scene, List<SceneRule> rules) {
        SceneDetailResp resp = new SceneDetailResp();
        BeanUtils.copyProperties(scene, resp);
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
