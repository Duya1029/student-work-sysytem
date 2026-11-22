package com.homework.mapper;

import com.homework.entity.Semester;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学期 Mapper 接口 - 对应 SemesterMapper.xml
 */
public interface SemesterMapper {

    // 查询全部学期
    List<Semester> selectAll();

    // 根据ID查询学期
    Semester selectById(@Param("semesterId") Long semesterId);

    // 查询当前学期
    Semester selectCurrent();

    // 新增学期
    int insert(Semester semester);

    // 修改学期
    int update(Semester semester);

    // 删除学期
    int deleteById(@Param("semesterId") Long semesterId);

    // 清除所有学期的当前标识
    int clearCurrent();

    // 设置当前学期
    int setCurrent(@Param("semesterId") Long semesterId);
}
