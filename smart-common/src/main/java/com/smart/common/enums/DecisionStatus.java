package com.smart.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 决策状态枚举
 * <p>
 * 定义决策在其生命周期中的各种状态。一个决策从创建到最终完成，
 * 会依次经历 PENDING -> EXECUTING -> COMPLETED/FAILED 的状态流转。
 * </p>
 * <p>
 * 设计说明：通过状态枚举实现决策执行的状态机管理，
 * 便于追踪决策进度、防止重复执行、支持失败重试等场景。
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum DecisionStatus {

    /**
     * 待处理 —— 决策已创建但尚未开始执行，等待决策引擎调度
     */
    PENDING(0, "待处理"),

    /**
     * 执行中 —— 决策正在执行，可能处于规则匹配、RAG 检索或 AI 推理等阶段
     */
    EXECUTING(1, "执行中"),

    /**
     * 已完成 —— 决策执行成功，指令已成功下发到目标设备
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败 —— 决策执行过程中出现异常，需要人工排查或自动重试
     */
    FAILED(3, "失败");

    /** 决策状态编码，用于数据库持久化存储和接口传输 */
    private final int code;

    /** 决策状态的中文描述，用于前端展示和日志输出 */
    private final String description;
}
