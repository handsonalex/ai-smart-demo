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

/**
 * 电站管理控制器 —— 接口层（Interfaces Layer）
 *
 * <p>提供电站资源的 RESTful CRUD 接口，遵循 REST 风格设计：
 * <ul>
 *   <li>POST   /api/v1/stations       — 创建电站</li>
 *   <li>GET    /api/v1/stations/{id}  — 查询单个电站详情</li>
 *   <li>GET    /api/v1/stations       — 分页查询电站列表</li>
 *   <li>DELETE /api/v1/stations/{id}  — 删除电站</li>
 * </ul>
 *
 * <p>设计要点：
 * <ol>
 *   <li>Controller 只负责 HTTP 协议适配，不包含业务逻辑</li>
 *   <li>通过 {@link StationCreateReq} 接收前端参数，通过 {@link StationResp} 返回响应，实现接口层与领域层的解耦</li>
 *   <li>使用 {@code @Valid} 触发 JSR 380 参数校验，校验失败由全局异常处理器统一返回错误信息</li>
 *   <li>使用 {@code @RequiredArgsConstructor} 通过构造器注入依赖，符合 Spring 推荐的不可变依赖注入方式</li>
 * </ol>
 *
 * @author Joseph Ho
 */
@Tag(name = "电站管理")
@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    /** 电站应用服务，负责编排业务逻辑（应用层） */
    private final StationAppService stationAppService;

    /**
     * 创建电站
     *
     * <p>处理流程：参数校验 → DTO 转领域对象 → 调用应用服务创建 → 领域对象转响应 DTO
     *
     * @param req 创建电站请求体，包含电站名称、位置、装机容量等信息，由 {@code @Valid} 触发参数校验
     * @return 创建成功的电站信息
     */
    @Operation(summary = "创建电站")
    @PostMapping
    public Result<StationResp> create(@Valid @RequestBody StationCreateReq req) {
        // 第一步：将请求 DTO 转换为领域实体（接口层 → 领域层的适配）
        Station station = new Station();
        BeanUtils.copyProperties(req, station);
        // 第二步：调用应用服务执行创建逻辑
        Station created = stationAppService.create(station);
        // 第三步：将领域实体转换为响应 DTO 返回给前端
        return Result.success(toResp(created));
    }

    /**
     * 查询电站详情
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务查询 → 领域对象转响应 DTO
     *
     * @param id 电站主键 ID，通过 URL 路径变量传入
     * @return 电站详情信息
     */
    @Operation(summary = "查询电站详情")
    @GetMapping("/{id}")
    public Result<StationResp> getById(@PathVariable Long id) {
        Station station = stationAppService.getById(id);
        return Result.success(toResp(station));
    }

    /**
     * 分页查询电站列表
     *
     * <p>处理流程：接收分页参数 → 调用应用服务分页查询 → 批量转换为响应 DTO → 封装分页结果
     *
     * @param pageNum  页码，默认值为 1
     * @param pageSize 每页大小，默认值为 10
     * @return 分页结果，包含总数、当前页、每页大小和电站列表
     */
    @Operation(summary = "分页查询电站")
    @GetMapping
    public Result<PageResult<StationResp>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Station> page = stationAppService.page(pageNum, pageSize);
        // 将领域实体列表批量转换为响应 DTO 列表
        List<StationResp> respList = page.getRecords().stream().map(this::toResp).toList();
        // 封装分页元数据（总记录数、当前页码、每页大小）和数据列表
        return Result.success(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), respList));
    }

    /**
     * 删除电站
     *
     * <p>处理流程：路径参数获取 ID → 调用应用服务执行删除
     *
     * @param id 要删除的电站 ID
     * @return 空响应，表示删除成功
     */
    @Operation(summary = "删除电站")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stationAppService.delete(id);
        return Result.success();
    }

    /**
     * 领域实体转响应 DTO 的私有方法
     *
     * <p>使用 Spring 的 {@link BeanUtils#copyProperties} 进行同名属性的浅拷贝，
     * 避免在 Controller 中暴露领域模型的内部结构
     *
     * @param station 电站领域实体
     * @return 电站响应 DTO
     */
    private StationResp toResp(Station station) {
        StationResp resp = new StationResp();
        BeanUtils.copyProperties(station, resp);
        return resp;
    }
}
