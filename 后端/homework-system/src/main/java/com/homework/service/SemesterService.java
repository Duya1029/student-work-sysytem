package com.homework.service;

import com.homework.entity.Semester;

import java.util.List;

/**
 * 学期服务接口（学期管理、设置当前学期）
 */
public interface SemesterService {

    /**
     * 查询全部学期
     */
    // 查询系统中全部学期列表
    List<Semester> getAllSemesters();

    /**
     * 根据ID查询学期
     */
    // 根据学期ID查询学期详细信息
    Semester getSemesterById(Long semesterId);

    /**
     * 查询当前学期
     */
    // 查询当前正在使用的学期
    Semester getCurrentSemester();

    /**
     * 新增学期
     */
    // 新增一条学期记录
    void addSemester(Semester semester);

    /**
     * 修改学期
     */
    // 修改学期信息
    void updateSemester(Semester semester);

    /**
     * 删除学期
     */
    // 根据学期ID删除学期
    void deleteSemester(Long semesterId);

    /**
     * 设置当前学期（同时清除其他学期的当前标识）
     */
    // 设置指定学期为当前学期，同时清除其他学期的当前标识
    void setCurrentSemester(Long semesterId);
}
