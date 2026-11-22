package com.homework.service;

import com.homework.entity.Homework;

import java.util.List;

/**
 * 作业服务接口（管理员视角的作业记录查询）
 */
public interface HomeworkService {

    /**
     * 管理员视角：分页查询全部作业
     */
    // 管理员视角下分页查询全部作业，支持按班级名称、教师姓名筛选
    List<Homework> getAllHomeworkList(int pageNum, int pageSize, String className, String teacherName);

    /**
     * 管理员视角：查询作业总数
     */
    // 管理员视角下查询符合条件的作业总记录数，用于分页
    int getAllHomeworkCount(String className, String teacherName);

    /**
     * 删除作业（管理员）
     */
    // 管理员根据作业ID删除作业
    void deleteHomework(Long homeworkId);
}
