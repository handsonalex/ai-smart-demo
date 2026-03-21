package com.smart.common.result;

import com.smart.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 返回结果封装类
 * <p>
 * 所有 REST API 接口统一使用此类作为响应体，确保前后端交互格式一致。
 * 包含三个核心字段：状态码（code）、提示信息（message）、业务数据（data）。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>使用泛型 T 支持任意类型的业务数据，保证类型安全</li>
 *   <li>实现 {@link Serializable} 接口，支持序列化传输（如 RPC 调用场景）</li>
 *   <li>提供静态工厂方法 success/fail，简化创建过程，统一构建逻辑</li>
 * </ul>
 * </p>
 *
 * @param <T> 业务数据的类型
 * @author Joseph Ho
 */
@Data
public class Result<T> implements Serializable {

    /** 响应状态码，0 表示成功，非 0 表示失败，具体含义参见 {@link ErrorCode} */
    private int code;

    /** 响应提示信息，成功时为 "成功"，失败时为具体的错误描述 */
    private String message;

    /** 响应业务数据，失败时通常为 null */
    private T data;

    /**
     * 构建成功响应（携带数据）
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 包含成功状态码和业务数据的 Result 对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        // 设置成功状态码和提示信息
        result.setCode(ErrorCode.SUCCESS.getCode());
        result.setMessage(ErrorCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 构建成功响应（不携带数据）
     * <p>
     * 适用于无需返回业务数据的操作，如删除、更新等。
     * </p>
     *
     * @param <T> 数据类型
     * @return 包含成功状态码的 Result 对象，data 为 null
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 通过错误码枚举构建失败响应
     *
     * @param errorCode 错误码枚举，提供错误码和错误信息
     * @param <T>       数据类型
     * @return 包含错误状态码和错误信息的 Result 对象
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        Result<T> result = new Result<>();
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage());
        return result;
    }

    /**
     * 通过自定义错误码和错误信息构建失败响应
     * <p>
     * 适用于需要动态构建错误码或错误信息的场景。
     * </p>
     *
     * @param code    自定义错误码
     * @param message 自定义错误信息
     * @param <T>     数据类型
     * @return 包含自定义错误码和错误信息的 Result 对象
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
