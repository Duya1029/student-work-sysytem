package com.homework.mapper;

import com.homework.entity.SystemLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统日志 Mapper 接口 - 对应 SystemLogMapper.xml
 */
public interface SystemLogMapper {

    // 分页查询日志列表（支持按用户名、日志类型、时间范围筛选）
    List<SystemLog> selectList(@Param("offset") int offset,
                               @Param("pageSize") int pageSize,
                               @Param("username") String username,
                               @Param("logType") String logType);

    // 查询日志总数
    int selectCount(@Param("username") String username,
                    @Param("logType") String logType);

    // 新增日志
    int insert(SystemLog log);
}
