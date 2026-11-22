package com.homework.mapper;

import com.homework.entity.Clazz;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级 Mapper 接口 - 对应 ClassMapper.xml
 */
public interface ClassMapper {

    // 分页查询班级列表（含学生人数）
    List<Clazz> selectList(@Param("offset") int offset,
                           @Param("pageSize") int pageSize,
                           @Param("className") String className);

    // 查询班级总数
    int selectCount(@Param("className") String className);

    // 查询所有班级（下拉列表用）
    List<Clazz> selectAll();

    // 根据ID查询班级
    Clazz selectById(@Param("classId") Long classId);

    // 新增班级
    int insert(Clazz clazz);

    // 修改班级
    int update(Clazz clazz);

    // 删除班级
    int deleteById(@Param("classId") Long classId);
}
