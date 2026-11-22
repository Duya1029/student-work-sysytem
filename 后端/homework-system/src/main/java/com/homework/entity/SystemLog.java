package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 系统日志实体类 - 对应 sys_log 表
 */
public class SystemLog {
    /** 日志ID */
    private Long logId; // 日志ID，主键
    /** 操作用户ID */
    private Long userId; // 操作用户ID
    /** 操作用户名 */
    private String username; // 操作用户名
    /** 操作描述 */
    private String operation; // 操作描述
    /** 请求方法 */
    private String method; // 请求方法
    /** 请求参数 */
    private String params; // 请求参数
    /** 请求IP */
    private String ip; // 请求IP
    /** 日志类型：LOGIN-登录，OPERATION-操作，ERROR-异常 */
    private String logType; // 日志类型：LOGIN-登录，OPERATION-操作，ERROR-异常
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
