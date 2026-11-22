package com.homework.common;

/**
 * 统一响应体
 * 所有接口统一返回此格式：{ code, message, data }
 */
// 统一响应结果类，使用泛型 T 表示返回数据的类型
public class Result<T> {

    /** 状态码：200-成功，400-参数错误，401-未登录，403-无权限，500-服务器错误 */
    // 响应状态码，标识请求处理结果
    private Integer code;

    /** 提示信息 */
    // 响应提示信息，向调用方说明处理结果
    private String message;

    /** 返回数据 */
    // 响应返回的实际数据，类型由泛型 T 指定
    private T data;

    // 私有无参构造方法，禁止外部直接 new 对象，只能通过静态工厂方法创建
    private Result() {}

    // 私有有参构造方法，用于内部创建 Result 对象
    private Result(Integer code, String message, T data) {
        // 设置状态码
        this.code = code;
        // 设置提示信息
        this.message = message;
        // 设置返回数据
        this.data = data;
    }

    // 获取状态码
    public Integer getCode() {
        return code;
    }

    // 设置状态码
    public void setCode(Integer code) {
        this.code = code;
    }

    // 获取提示信息
    public String getMessage() {
        return message;
    }

    // 设置提示信息
    public void setMessage(String message) {
        this.message = message;
    }

    // 获取返回数据
    public T getData() {
        return data;
    }

    // 设置返回数据
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    // 创建成功响应对象，不带数据，默认消息为"操作成功"
    public static <T> Result<T> success() {
        // 返回状态码 200、消息"操作成功"、数据为 null 的响应
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     */
    // 创建成功响应对象，携带返回数据，默认消息为"操作成功"
    public static <T> Result<T> success(T data) {
        // 返回状态码 200、消息"操作成功"、携带数据的响应
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    // 创建成功响应对象，携带自定义消息和返回数据
    public static <T> Result<T> success(String message, T data) {
        // 返回状态码 200、自定义消息和数据的响应
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应
     */
    // 创建失败响应对象，指定状态码和错误消息
    public static <T> Result<T> error(Integer code, String message) {
        // 返回指定状态码、错误消息、数据为 null 的响应
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（默认500）
     */
    // 创建失败响应对象，使用默认状态码 500 和指定错误消息
    public static <T> Result<T> error(String message) {
        // 返回状态码 500、指定错误消息、数据为 null 的响应
        return new Result<>(500, message, null);
    }
}
