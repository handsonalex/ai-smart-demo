package com.smart.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Joseph Ho
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(10000, "系统异常"),

    /**
     * 参数错误
     */
    PARAM_ERROR(10001, "参数错误"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(10002, "数据不存在"),

    /**
     * 重复操作
     */
    DUPLICATE_OPERATION(10003, "重复操作"),

    /**
     * 电站不存在
     */
    STATION_NOT_FOUND(20001, "电站不存在"),

    /**
     * 设备不存在
     */
    DEVICE_NOT_FOUND(20002, "设备不存在"),

    /**
     * 场景不存在
     */
    SCENE_NOT_FOUND(20003, "场景不存在"),

    /**
     * 决策执行失败
     */
    DECISION_FAILED(30001, "决策执行失败"),

    /**
     * RAG 检索失败
     */
    RAG_RETRIEVAL_FAILED(30002, "RAG检索失败"),

    /**
     * AI 推理失败
     */
    AI_INFERENCE_FAILED(30003, "AI推理失败");

    private final int code;

    private final String message;
}
