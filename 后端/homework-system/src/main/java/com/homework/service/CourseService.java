package com.homework.service;

import com.homework.entity.Course;

import java.util.List;

/**
 * 课程服务接口（课程管理、按教师/班级查询）
 */
public interface CourseService {

    /**
     * 分页查询课程列表
     */
    // 分页查询课程列表，支持按课程名称、教师ID、学期ID筛选
    List<Course> getCourseList(int pageNum, int pageSize, String courseName, Long teacherId, Long semesterId);

    /**
     * 查询课程总数
     */
    // 查询符合条件的课程总记录数，用于分页
    int getCourseCount(String courseName, Long teacherId, Long semesterId);

    /**
     * 根据教师ID查询课程列表
     */
    // 根据教师ID查询该教师所教的所有课程
    List<Course> getCoursesByTeacherId(Long teacherId);

    /**
     * 根据班级ID查询课程列表
     */
    // 根据班级ID查询该班级开设的所有课程
    List<Course> getCoursesByClassId(Long classId);

    /**
     * 根据ID查询课程
     */
    // 根据课程ID查询课程详细信息
    Course getCourseById(Long courseId);

    /**
     * 新增课程（同时关联班级）
     */
    // 新增课程，并同时关联其所属班级
    void addCourse(Course course);

    /**
     * 修改课程（同时更新关联班级）
     */
    // 修改课程信息，并同步更新课程与班级的关联关系
    void updateCourse(Course course);

    /**
     * 删除课程（同时清理关联班级）
     */
    // 根据课程ID删除课程，并清理课程与班级的关联数据
    void deleteCourse(Long courseId);
}
