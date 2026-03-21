package com.smart.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于封装系统中所有可预见的业务逻辑异常。继承自 {@link RuntimeException}，
 * 属于非受检异常，无需在方法签名中显式声明。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>统一使用 {@link ErrorCode} 枚举来定义错误码和错误信息，保证异常信息的规范化</li>
 *   <li>配合全局异常处理器（GlobalExceptionHandler）使用，自动将业务异常转换为统一的 API 响应格式</li>
 *   <li>与系统异常（如 NullPointerException、SQLException）区分开来，便于分层处理</li>
 * </ul>
 * </p>
 *
 * @author Joseph Ho
 */
@Getter
public class BizException extends RuntimeException {

    /** 业务错误码，对应 {@link ErrorCode} 中定义的 code 值 */
    private final int code;

    /**
     * 通过错误码枚举构造业务异常
     * <p>
     * 使用错误码枚举中预定义的错误信息作为异常消息。
     * 适用于标准化的业务异常场景。
     * </p>
     *
     * @param errorCode 错误码枚举，包含错误码和默认错误信息
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 通过错误码枚举和自定义消息构造业务异常
     * <p>
     * 使用错误码枚举中的 code，但允许自定义错误信息。
     * 适用于需要提供更具体错误描述的场景，例如："电站不存在，stationId=123"。
     * </p>
     *
     * @param errorCode 错误码枚举，提供错误码
     * @param message   自定义的错误描述信息
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
