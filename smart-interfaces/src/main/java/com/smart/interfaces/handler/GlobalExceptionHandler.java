package com.smart.interfaces.handler;

import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>基于 Spring 的 {@code @RestControllerAdvice} 机制，拦截所有 Controller 抛出的异常，
 * 统一转换为标准的 {@link Result} 响应格式返回给前端，避免将异常堆栈直接暴露给调用方。
 *
 * <p>异常处理策略（按优先级从高到低）：
 * <ol>
 *   <li><strong>业务异常（BizException）</strong>：已知的业务错误，直接返回业务错误码和消息，日志级别为 WARN</li>
 *   <li><strong>参数校验异常（MethodArgumentNotValidException）</strong>：{@code @Valid} 校验 @RequestBody 参数失败时抛出，
 *       提取第一个字段错误信息返回，日志级别为 WARN</li>
 *   <li><strong>参数绑定异常（BindException）</strong>：非 @RequestBody 参数（如表单、URL参数）校验失败时抛出，
 *       处理逻辑与参数校验异常类似，日志级别为 WARN</li>
 *   <li><strong>未知异常（Exception）</strong>：兜底处理，捕获所有未预期的异常，返回系统错误码，
 *       日志级别为 ERROR 并记录完整堆栈，便于排查问题</li>
 * </ol>
 *
 * <p>设计要点：
 * <ul>
 *   <li>所有异常都返回统一的 {@link Result} 格式，前端只需关注 code 和 message 字段</li>
 *   <li>业务异常使用 WARN 级别日志（预期内的错误），系统异常使用 ERROR 级别（需要关注的非预期错误）</li>
 *   <li>参数校验失败统一使用 {@link ErrorCode#PARAM_ERROR} 错误码</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * <p>业务异常是应用层或领域层主动抛出的已知异常（如"电站不存在"、"设备已离线"等），
     * 携带明确的错误码和错误消息，直接透传给前端。
     *
     * @param e 业务异常，包含错误码和错误消息
     * @return 包含业务错误码和消息的标准响应
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @RequestBody 参数校验异常
     *
     * <p>当 Controller 方法参数标注了 {@code @Valid}，且 @RequestBody 中的字段
     * 不满足 JSR 380 校验注解（如 @NotBlank、@NotNull）时，Spring 会抛出此异常。
     * 此处提取第一个校验失败的字段错误消息返回给前端。
     *
     * @param e 参数校验异常
     * @return 包含参数错误码和校验失败消息的标准响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        // 提取第一个字段校验错误的默认消息（即注解中 message 属性的值）
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", message);
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理参数绑定异常
     *
     * <p>当非 @RequestBody 参数（如 @ModelAttribute、表单参数、URL 查询参数）
     * 校验失败时抛出此异常。处理逻辑与 {@link #handleValidException} 类似。
     *
     * @param e 参数绑定异常
     * @return 包含参数错误码和绑定失败消息的标准响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败: {}", message);
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 兜底异常处理 —— 捕获所有未被上述处理器匹配的异常
     *
     * <p>这是最后一道防线，处理所有未预期的系统异常（如 NullPointerException、数据库连接异常等）。
     * 使用 ERROR 级别日志记录完整的异常堆栈，便于开发人员排查问题。
     * 返回通用的系统错误码，不向前端暴露具体的异常信息。
     *
     * @param e 未知异常
     * @return 包含系统错误码的标准响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }
}
