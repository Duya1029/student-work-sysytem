package com.homework.config;

import com.homework.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获 Controller 层抛出的异常，统一返回 Result 格式
 */
// @RestControllerAdvice 组合了 @ControllerAdvice 和 @ResponseBody，
// 用于全局捕获 Controller 层抛出的异常，并以 JSON 格式返回统一响应
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理非法参数异常
     */
    // 处理 IllegalArgumentException 类型的异常（如参数校验失败）
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        // 返回状态码 400 和异常消息，表示请求参数错误
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    // 处理 RuntimeException 及其子类异常（业务逻辑运行时错误）
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        // 打印异常堆栈信息，便于开发调试和排错
        e.printStackTrace();
        // 返回状态码 500 和拼接的错误消息，表示服务器内部错误
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }

    /**
     * 处理其他异常
     */
    // 处理 Exception 类型的异常，作为兜底处理器捕获所有未被上述方法处理的异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 打印异常堆栈信息，便于开发调试和排错
        e.printStackTrace();
        // 返回状态码 500 和拼接的错误消息，表示系统级异常
        return Result.error(500, "系统异常：" + e.getMessage());
    }
}
