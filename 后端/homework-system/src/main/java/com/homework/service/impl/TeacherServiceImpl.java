package com.homework.service.impl;

import com.homework.dto.GradeDTO;
import com.homework.dto.HomeworkDTO;
import com.homework.entity.Clazz;
import com.homework.entity.Course;
import com.homework.entity.Homework;
import com.homework.entity.Submission;
import com.homework.entity.User;
import com.homework.mapper.ClassMapper;
import com.homework.mapper.CourseMapper;
import com.homework.mapper.HomeworkMapper;
import com.homework.mapper.SubmissionMapper;
import com.homework.mapper.UserMapper;
import com.homework.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private HomeworkMapper homeworkMapper; // 作业数据访问对象，用于操作作业表

    @Autowired
    private SubmissionMapper submissionMapper; // 提交记录数据访问对象，用于操作作业提交表

    @Autowired
    private ClassMapper classMapper; // 班级数据访问对象，用于操作班级表

    @Autowired
    private UserMapper userMapper; // 用户数据访问对象，用于操作用户表

    @Autowired
    private CourseMapper courseMapper; // 课程数据访问对象，用于操作课程表

    @Override
    public void publishHomework(HomeworkDTO dto, Long teacherId) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) { // 校验作业标题是否为空
            throw new IllegalArgumentException("作业标题不能为空"); // 标题为空则抛出异常
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) { // 校验作业描述是否为空
            throw new IllegalArgumentException("作业描述不能为空"); // 描述为空则抛出异常
        }
        if (dto.getClassId() == null) { // 校验班级ID是否为空
            throw new IllegalArgumentException("请选择班级"); // 班级ID为空则抛出异常
        }
        if (dto.getDeadline() == null || dto.getDeadline().trim().isEmpty()) { // 校验截止时间是否为空
            throw new IllegalArgumentException("截止时间不能为空"); // 截止时间为空则抛出异常
        }

        Homework homework = new Homework(); // 创建作业实体对象
        homework.setTitle(dto.getTitle().trim()); // 设置去除空格后的作业标题
        homework.setDescription(dto.getDescription().trim()); // 设置去除空格后的作业描述
        homework.setTeacherId(teacherId); // 设置发布作业的教师ID
        homework.setClassId(dto.getClassId()); // 设置作业所属班级ID
        // 解析截止时间字符串
        homework.setDeadline(LocalDateTime.parse(dto.getDeadline(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))); // 将字符串格式的截止时间解析为LocalDateTime对象

        homeworkMapper.insert(homework); // 调用Mapper将作业信息插入数据库
    }

    @Override
    public List<Homework> getMyHomeworkList(Long teacherId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return homeworkMapper.selectListByTeacher(teacherId, offset, pageSize); // 调用Mapper分页查询指定教师发布的作业列表
    }

    @Override
    public int getMyHomeworkCount(Long teacherId) {
        return homeworkMapper.selectCountByTeacher(teacherId); // 调用Mapper查询指定教师发布的作业总数
    }

    @Override
    public List<Submission> getSubmissionsByHomeworkId(Long homeworkId) {
        return submissionMapper.selectByHomeworkId(homeworkId); // 调用Mapper查询指定作业的所有提交记录
    }

    @Override
    public void gradeSubmission(GradeDTO dto) {
        if (dto.getSubmissionId() == null) { // 校验提交记录ID是否为空
            throw new IllegalArgumentException("提交记录ID不能为空"); // ID为空则抛出异常
        }
        if (dto.getScore() == null || dto.getScore() < 0 || dto.getScore() > 100) { // 校验分数是否在0-100范围内
            throw new IllegalArgumentException("分数必须在0-100之间"); // 分数不合法则抛出异常
        }
        // 使用扩展版批改，支持 is_returned 和 is_excellent
        Boolean isReturned = dto.getIsReturned() != null ? dto.getIsReturned() : false; // 获取是否打回标志，默认为false
        Boolean isExcellent = dto.getIsExcellent() != null ? dto.getIsExcellent() : false; // 获取是否优秀标志，默认为false
        submissionMapper.updateGradeExt(dto.getSubmissionId(), dto.getScore(),
                dto.getComment(), isReturned, isExcellent); // 调用Mapper扩展版更新提交记录的成绩、评语、打回和优秀状态
    }

    @Override
    public List<Clazz> getMyClasses(Long teacherId) {
        // 教师可以管理所有班级（简化逻辑，实际项目可设计教师-班级关联表）
        return classMapper.selectAll(); // 调用Mapper查询所有班级列表并返回
    }

    @Override
    public Map<String, Object> getHomeworkStatistics(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId); // 根据作业ID查询作业信息
        if (homework == null) { // 判断作业是否存在
            throw new IllegalArgumentException("作业不存在"); // 作业不存在则抛出异常
        }
        // 已交人数
        List<Submission> submissions = submissionMapper.selectByHomeworkId(homeworkId); // 查询该作业的所有提交记录
        int submittedCount = submissions.size(); // 已提交人数为提交记录列表的大小
        // 班级总人数
        int totalStudents = userMapper.selectCount("STUDENT", null, homework.getClassId()); // 查询该班级的学生总数
        int unsubmittedCount = Math.max(totalStudents - submittedCount, 0); // 未提交人数=总人数-已提交人数，且不小于0
        Map<String, Object> stats = new HashMap<>(); // 创建统计结果Map集合
        stats.put("homeworkId", homeworkId); // 将作业ID放入Map
        stats.put("totalStudents", totalStudents); // 将班级总人数放入Map
        stats.put("submittedCount", submittedCount); // 将已提交人数放入Map
        stats.put("unsubmittedCount", unsubmittedCount); // 将未提交人数放入Map
        return stats; // 返回作业提交统计信息
    }

    @Override
    public List<User> getCourseStudents(Long courseId) {
        if (courseId == null) { // 校验课程ID是否为空
            throw new IllegalArgumentException("课程ID不能为空"); // ID为空则抛出异常
        }
        Course course = courseMapper.selectById(courseId); // 根据课程ID查询课程信息
        if (course == null) { // 判断课程是否存在
            throw new IllegalArgumentException("课程不存在"); // 课程不存在则抛出异常
        }
        List<User> allStudents = new ArrayList<>(); // 创建用于存放所有学生的列表
        // 课程关联的班级ID列表（逗号分隔字符串）
        String classIdsStr = course.getClassIds(); // 获取课程关联的班级ID字符串
        if (classIdsStr == null || classIdsStr.trim().isEmpty()) { // 判断班级ID字符串是否为空
            return allStudents; // 为空则直接返回空列表
        }
        List<String> classIdList = Arrays.asList(classIdsStr.split(",")); // 将逗号分隔的字符串拆分为班级ID列表
        for (String classIdStr : classIdList) { // 遍历每个班级ID字符串
            String trimmed = classIdStr.trim(); // 去除班级ID字符串前后空格
            if (trimmed.isEmpty()) { // 判断去除空格后是否为空
                continue; // 为空则跳过当前循环
            }
            try {
                Long classId = Long.parseLong(trimmed); // 将班级ID字符串转换为Long类型
                // 查询该班级的所有学生
                List<User> students = userMapper.selectList(0, 1000, "STUDENT", null, classId); // 查询指定班级的所有学生（最多1000条）
                allStudents.addAll(students); // 将该班级学生添加到总列表中
            } catch (NumberFormatException e) {
                // 跳过无法解析的班级ID
            }
        }
        return allStudents; // 返回课程关联的所有学生列表
    }

    @Override
    public Map<String, Object> getGradeStatistics(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId); // 根据作业ID查询作业信息
        if (homework == null) { // 判断作业是否存在
            throw new IllegalArgumentException("作业不存在"); // 作业不存在则抛出异常
        }
        List<Submission> submissions = submissionMapper.selectByHomeworkId(homeworkId); // 查询该作业的所有提交记录
        // 统计分数分布
        int excellent = 0;  // 90-100分（优秀）的人数
        int good = 0;       // 80-89分（良好）的人数
        int medium = 0;     // 70-79分（中等）的人数
        int pass = 0;       // 60-69分（及格）的人数
        int fail = 0;       // 0-59分（不及格）的人数
        int ungraded = 0;   // 未批改的人数
        int totalScore = 0; // 已批改作业的总分
        int gradedCount = 0; // 已批改的作业数量

        for (Submission s : submissions) { // 遍历每条提交记录
            if (s.getScore() == null) { // 判断该提交记录是否尚未批改
                ungraded++; // 未批改计数加1
            } else {
                int score = s.getScore(); // 获取该提交记录的分数
                if (score >= 90) { // 判断分数是否在90分及以上
                    excellent++; // 优秀人数加1
                } else if (score >= 80) { // 判断分数是否在80-89分之间
                    good++; // 良好人数加1
                } else if (score >= 70) { // 判断分数是否在70-79分之间
                    medium++; // 中等人数加1
                } else if (score >= 60) { // 判断分数是否在60-69分之间
                    pass++; // 及格人数加1
                } else { // 分数在60分以下
                    fail++; // 不及格人数加1
                }
                totalScore += score; // 累加到总分
                gradedCount++; // 已批改数量加1
            }
        }

        Map<String, Object> stats = new HashMap<>(); // 创建统计结果Map集合
        stats.put("homeworkId", homeworkId); // 将作业ID放入Map
        stats.put("homeworkTitle", homework.getTitle()); // 将作业标题放入Map
        stats.put("totalSubmissions", submissions.size()); // 将总提交数放入Map
        stats.put("gradedCount", gradedCount); // 将已批改数量放入Map
        stats.put("ungradedCount", ungraded); // 将未批改数量放入Map
        stats.put("averageScore", gradedCount > 0 ? (double) totalScore / gradedCount : 0); // 计算平均分（无批改时为0）并放入Map

        // 分数分布
        Map<String, Integer> distribution = new HashMap<>(); // 创建分数分布Map
        distribution.put("excellent", excellent);  // 90-100分的人数
        distribution.put("good", good);            // 80-89分的人数
        distribution.put("medium", medium);        // 70-79分的人数
        distribution.put("pass", pass);            // 60-69分的人数
        distribution.put("fail", fail);             // 0-59分的人数
        stats.put("distribution", distribution); // 将分数分布Map放入统计结果Map

        return stats; // 返回成绩统计信息
    }
}
