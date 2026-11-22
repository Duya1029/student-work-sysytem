package com.homework.service;

import com.homework.dto.SubmissionDTO;
import com.homework.entity.Homework;
import com.homework.entity.Submission;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口（查看作业、提交作业、查看分数评语、课程查询、首页统计）
 */
public interface StudentService {

    /**
     * 查看我的作业列表（分页）
     * 返回作业列表，每条作业包含学生的提交信息（若有）
     */
    // 根据班级ID分页查询学生的作业列表，每条作业附带该学生的提交信息
    List<Homework> getMyHomeworkList(Long classId, int pageNum, int pageSize);

    /**
     * 查看我的作业总数
     */
    // 根据班级ID查询学生的作业总记录数，用于分页
    int getMyHomeworkCount(Long classId);

    /**
     * 提交作业（若已提交则更新内容）
     */
    // 学生提交作业，若已存在提交记录则更新提交内容
    void submitHomework(SubmissionDTO dto, Long studentId);

    /**
     * 查看作业详情及分数评语
     * 返回作业信息 + 当前学生的提交信息（分数、评语、是否打回、是否优秀）
     */
    // 查询作业详情，返回当前学生的提交记录，包括分数、评语、是否打回及优秀标记
    Submission getHomeworkDetail(Long homeworkId, Long studentId);

    /**
     * 查看历史成绩（已批改的提交记录，含评语、是否打回、是否优秀）
     */
    // 查询学生历史已批改作业记录，包含评语、打回及优秀标记
    List<Submission> getMyGrades(Long studentId);

    /**
     * 获取学生首页统计数据（待提交数、已批改数、近期截止作业）
     */
    // 获取学生首页统计数据，包括待提交数、已批改数及近期截止作业
    Map<String, Object> getDashboard(Long studentId, Long classId);
}
