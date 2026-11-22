package com.homework.service.impl;

import com.homework.dto.SubmissionDTO;
import com.homework.entity.Homework;
import com.homework.entity.Submission;
import com.homework.mapper.HomeworkMapper;
import com.homework.mapper.SubmissionMapper;
import com.homework.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private HomeworkMapper homeworkMapper; // 作业数据访问对象，用于操作作业表

    @Autowired
    private SubmissionMapper submissionMapper; // 提交记录数据访问对象，用于操作作业提交表

    @Override
    public List<Homework> getMyHomeworkList(Long classId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return homeworkMapper.selectListForStudent(classId, null, offset, pageSize); // 调用Mapper分页查询指定班级的作业列表（不限学生）
    }

    @Override
    public int getMyHomeworkCount(Long classId) {
        return homeworkMapper.selectCountForStudent(classId); // 调用Mapper查询指定班级的作业总数
    }

    @Override
    public void submitHomework(SubmissionDTO dto, Long studentId) {
        if (dto.getHomeworkId() == null) { // 校验作业ID是否为空
            throw new IllegalArgumentException("作业ID不能为空"); // ID为空则抛出异常
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) { // 校验作业内容是否为空
            throw new IllegalArgumentException("作业内容不能为空"); // 内容为空则抛出异常
        }

        // 查询是否已提交过
        Submission existing = submissionMapper.selectByHomeworkAndStudent(dto.getHomeworkId(), studentId); // 根据作业ID和学生ID查询提交记录

        if (existing == null) { // 判断是否为首次提交
            // 首次提交
            Submission submission = new Submission(); // 创建提交记录实体对象
            submission.setHomeworkId(dto.getHomeworkId()); // 设置作业ID
            submission.setStudentId(studentId); // 设置学生ID
            submission.setContent(dto.getContent().trim()); // 设置去除空格后的作业内容
            submission.setSubmitTime(LocalDateTime.now()); // 设置提交时间为当前时间
            submissionMapper.insert(submission); // 调用Mapper将提交记录插入数据库
        } else {
            // 重新提交（更新内容，清空分数和评语，需要教师重新批改）
            existing.setContent(dto.getContent().trim()); // 更新作业内容
            existing.setSubmitTime(LocalDateTime.now()); // 更新提交时间为当前时间
            submissionMapper.updateContent(existing); // 调用Mapper更新提交记录内容
        }
    }

    @Override
    public Submission getHomeworkDetail(Long homeworkId, Long studentId) {
        return submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId); // 根据作业ID和学生ID查询提交记录详情
    }

    @Override
    public List<Submission> getMyGrades(Long studentId) {
        // 返回已批改的提交记录，包含评语、是否打回、是否优秀字段
        return submissionMapper.selectByStudentId(studentId); // 调用Mapper查询指定学生的所有提交记录
    }

    @Override
    public Map<String, Object> getDashboard(Long studentId, Long classId) {
        Map<String, Object> dashboard = new HashMap<>(); // 创建仪表盘数据Map集合
        if (classId == null) { // 判断学生是否有班级
            dashboard.put("totalHomework", 0); // 总作业数设为0
            dashboard.put("submittedCount", 0); // 已提交数设为0
            dashboard.put("pendingCount", 0); // 待提交数设为0
            dashboard.put("gradedCount", 0); // 已批改数设为0
            dashboard.put("recentHomeworks", List.of()); // 近期作业设为空列表
            return dashboard; // 返回空仪表盘数据
        }
        // 该班级的作业总数
        int totalHomework = homeworkMapper.selectCountForStudent(classId); // 查询该班级的作业总数
        // 该学生的提交总数（含未批改）
        int submittedCount = submissionMapper.selectCountByStudent(studentId); // 查询该学生的提交总数
        // 待提交数 = 总作业数 - 已提交数
        int pendingCount = Math.max(totalHomework - submittedCount, 0); // 计算待提交数，且不小于0
        // 已批改数
        int gradedCount = submissionMapper.selectGradedCountByStudent(studentId); // 查询该学生已批改的作业数量
        // 近期作业（取最近5条）
        List<Homework> recentHomeworks = homeworkMapper.selectListForStudent(classId, studentId, 0, 5); // 查询最近5条作业

        dashboard.put("totalHomework", totalHomework); // 将总作业数放入Map
        dashboard.put("submittedCount", submittedCount); // 将已提交数放入Map
        dashboard.put("pendingCount", pendingCount); // 将待提交数放入Map
        dashboard.put("gradedCount", gradedCount); // 将已批改数放入Map
        dashboard.put("recentHomeworks", recentHomeworks); // 将近期作业列表放入Map
        return dashboard; // 返回学生仪表盘数据
    }
}
