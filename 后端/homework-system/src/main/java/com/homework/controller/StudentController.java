package com.homework.controller;

import com.homework.common.Result;
import com.homework.dto.PageResult;
import com.homework.dto.SubmissionDTO;
import com.homework.entity.Course;
import com.homework.entity.Homework;
import com.homework.entity.Submission;
import com.homework.entity.User;
import com.homework.service.CourseService;
import com.homework.service.StudentService;
import com.homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生控制器 - 查看作业、提交作业、查看分数评语、历史成绩、
 * 课程查询、首页统计、成绩查询（含评语、是否打回、是否优秀）
 * 所有接口前缀：/api/student
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    // 注入学生服务
    @Autowired
    private StudentService studentService;

    // 注入用户服务
    @Autowired
    private UserService userService;

    // 注入课程服务
    @Autowired
    private CourseService courseService;

    /**
     * 查看我的作业列表（分页）
     */
    @GetMapping("/homework/list")
    public Result<PageResult<Homework>> homeworkList(
            @RequestParam(defaultValue = "1") int pageNum,   // 当前页码
            @RequestParam(defaultValue = "10") int pageSize, // 每页条数
            HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 根据学生ID查询用户信息，获取所在班级ID
        User user = userService.getUserById(studentId);
        // 如果用户不存在或没有班级，返回空列表
        if (user == null || user.getClassId() == null) {
            // 返回空的分页结果
            return Result.success(new PageResult<>(0L, List.of(), pageNum, pageSize));
        }
        // 调用学生服务查询该班级的作业列表
        List<Homework> list = studentService.getMyHomeworkList(user.getClassId(), pageNum, pageSize);
        // 查询该班级的作业总数
        int total = studentService.getMyHomeworkCount(user.getClassId());
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 提交作业
     */
    @PostMapping("/homework/submit")
    public Result<Void> submitHomework(@RequestBody SubmissionDTO dto, HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 调用学生服务提交作业，传入提交内容和学生ID
        studentService.submitHomework(dto, studentId);
        // 返回提交成功
        return Result.success("提交成功", null);
    }

    /**
     * 查看作业详情及分数评语（含是否打回、是否优秀）
     */
    @GetMapping("/homework/detail/{homeworkId}")
    public Result<Submission> homeworkDetail(@PathVariable Long homeworkId, HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 调用学生服务查询该学生在某份作业上的提交详情（含成绩、评语）
        Submission submission = studentService.getHomeworkDetail(homeworkId, studentId);
        // 返回提交详情
        return Result.success(submission);
    }

    /**
     * 查看历史成绩（已批改的提交记录，含评语、是否打回、是否优秀）
     */
    @GetMapping("/homework/grades")
    public Result<List<Submission>> myGrades(HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 调用学生服务查询该学生的历史成绩列表
        return Result.success(studentService.getMyGrades(studentId));
    }

    // ==================== 课程查询 ====================

    /**
     * 查看我的课程列表（通过学生所在班级关联查询）
     */
    @GetMapping("/course/list")
    public Result<List<Course>> myCourseList(HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 查询用户信息获取班级ID
        User user = userService.getUserById(studentId);
        // 如果用户不存在或没有班级，返回空列表
        if (user == null || user.getClassId() == null) {
            // 返回空列表
            return Result.success(List.of());
        }
        // 调用课程服务查询该班级的所有课程
        return Result.success(courseService.getCoursesByClassId(user.getClassId()));
    }

    // ==================== 首页统计 ====================

    /**
     * 学生首页统计数据（待提交数、已批改数、近期截止作业）
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(HttpServletRequest request) {
        // 从请求域中获取当前学生ID
        Long studentId = (Long) request.getAttribute("userId");
        // 查询用户信息获取班级ID
        User user = userService.getUserById(studentId);
        // 获取班级ID，若用户不存在则为null
        Long classId = (user != null) ? user.getClassId() : null;
        // 调用学生服务获取首页统计数据
        return Result.success(studentService.getDashboard(studentId, classId));
    }
}
