package com.homework.service.impl;

import com.homework.entity.SystemLog;
import com.homework.mapper.SystemLogMapper;
import com.homework.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志服务实现类
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogMapper systemLogMapper; // 系统日志数据访问对象，用于操作系统日志表

    @Override
    public List<SystemLog> getLogList(int pageNum, int pageSize, String username, String logType) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return systemLogMapper.selectList(offset, pageSize, username, logType); // 调用Mapper分页查询系统日志列表
    }

    @Override
    public int getLogCount(String username, String logType) {
        return systemLogMapper.selectCount(username, logType); // 调用Mapper查询符合条件的日志总数
    }

    @Override
    public void addLog(SystemLog log) {
        if (log == null) { // 校验日志对象是否为空
            return; // 日志对象为空则直接返回，不做处理
        }
        systemLogMapper.insert(log); // 调用Mapper将日志信息插入数据库
    }

    @Override
    public void recordLog(Long userId, String username, String operation, String method, String params, String ip, String logType) {
        SystemLog log = new SystemLog(); // 创建系统日志实体对象
        log.setUserId(userId); // 设置操作用户ID
        log.setUsername(username); // 设置操作用户名
        log.setOperation(operation); // 设置操作描述
        log.setMethod(method); // 设置调用的方法名
        log.setParams(params); // 设置请求参数
        log.setIp(ip); // 设置操作来源IP
        log.setLogType(logType != null && !logType.trim().isEmpty() ? logType : "OPERATION"); // 设置日志类型，默认为OPERATION（操作日志）
        log.setCreateTime(LocalDateTime.now()); // 设置日志创建时间为当前时间
        systemLogMapper.insert(log); // 调用Mapper将日志信息插入数据库
    }
}
