package com.smart.interfaces.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.application.service.StationAppService;
import com.smart.common.result.PageResult;
import com.smart.common.result.Result;
import com.smart.domain.entity.Station;
import com.smart.interfaces.dto.request.StationCreateReq;
import com.smart.interfaces.dto.response.StationResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "电站管理")
@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationAppService stationAppService;

    @Operation(summary = "创建电站")
    @PostMapping
    public Result<StationResp> create(@Valid @RequestBody StationCreateReq req) {
        Station station = new Station();
        BeanUtils.copyProperties(req, station);
        Station created = stationAppService.create(station);
        return Result.success(toResp(created));
    }

    @Operation(summary = "查询电站详情")
    @GetMapping("/{id}")
    public Result<StationResp> getById(@PathVariable Long id) {
        Station station = stationAppService.getById(id);
        return Result.success(toResp(station));
    }

    @Operation(summary = "分页查询电站")
    @GetMapping
    public Result<PageResult<StationResp>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Station> page = stationAppService.page(pageNum, pageSize);
        List<StationResp> respList = page.getRecords().stream().map(this::toResp).toList();
        return Result.success(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), respList));
    }

    @Operation(summary = "删除电站")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stationAppService.delete(id);
        return Result.success();
    }

    private StationResp toResp(Station station) {
        StationResp resp = new StationResp();
        BeanUtils.copyProperties(station, resp);
        return resp;
    }
}
