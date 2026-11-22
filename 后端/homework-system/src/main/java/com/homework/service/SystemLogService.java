package com.homework.service;

import com.homework.entity.SystemLog;

import java.util.List;

/**
 * 系统日志服务接口（日志查询、记录）
 */
public interface SystemLogService {

    /**
     * 分页查询日志列表
     */
    // 分页查询系统日志列表，支持按用户名、日志类型筛选
    List<SystemLog> getLogList(int pageNum, int pageSize, String username, String logType);

    /**
     * 查询日志总数
     */
    // 查询符合条件的日志总记录数，用于分页
    int getLogCount(String username, String logType);

    /**
     * 新增日志
     */
    // 新增一条系统日志记录
    void addLog(SystemLog log);

    /**
     * 记录操作日志（简化方法）
     */
    // 记录用户操作日志的简化方法，写入用户、操作、方法、参数、IP及日志类型
    void recordLog(Long userId, String username, String operation, String method, String params, String ip, String logType);
}
