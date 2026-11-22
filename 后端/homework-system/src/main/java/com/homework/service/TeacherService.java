package com.homework.service;

import com.homework.dto.GradeDTO;
import com.homework.dto.HomeworkDTO;
import com.homework.entity.Clazz;
import com.homework.entity.Homework;
import com.homework.entity.Submission;
import com.homework.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 教师服务接口（发布作业、批改打分、查看提交列表、课程学生名单、成绩统计）
 */
public interface TeacherService {

    /**
     * 发布作业
     */
    // 教师发布作业，关联课程、班级等信息
    void publishHomework(HomeworkDTO dto, Long teacherId);

    /**
     * 查看我的作业列表（分页）
     */
    // 分页查询当前教师发布的作业列表
    List<Homework> getMyHomeworkList(Long teacherId, int pageNum, int pageSize);

    /**
     * 查看我的作业总数
     */
    // 查询当前教师发布的作业总记录数，用于分页
    int getMyHomeworkCount(Long teacherId);

    /**
     * 查看某作业的提交列表
     */
    // 根据作业ID查询所有学生的提交记录列表
    List<Submission> getSubmissionsByHomeworkId(Long homeworkId);

    /**
     * 批改作业（打分评语，支持打回、优秀标记）
     */
    // 批改学生作业提交，包括打分、评语、打回及优秀标记
    void gradeSubmission(GradeDTO dto);

    /**
     * 查看我的班级列表
     */
    // 查询当前教师所教的班级列表
    List<Clazz> getMyClasses(Long teacherId);

    /**
     * 查看某作业的完成统计（已交人数、未交人数）
     */
    // 查询某作业的完成情况统计，包括已交人数和未交人数
    Map<String, Object> getHomeworkStatistics(Long homeworkId);

    /**
     * 查询某课程的学生名单（通过课程关联的班级查询学生）
     */
    // 根据课程ID查询该课程下所有班级的学生名单
    List<User> getCourseStudents(Long courseId);

    /**
     * 查询某作业的成绩统计（分数分布）
     */
    // 查询某作业的成绩分布统计
    Map<String, Object> getGradeStatistics(Long homeworkId);
}
