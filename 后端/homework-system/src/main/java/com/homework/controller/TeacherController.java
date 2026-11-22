package com.homework.controller;

import com.homework.common.Result;
import com.homework.dto.GradeDTO;
import com.homework.dto.HomeworkDTO;
import com.homework.dto.PageResult;
import com.homework.entity.Announcement;
import com.homework.entity.Clazz;
import com.homework.entity.Course;
import com.homework.entity.Homework;
import com.homework.entity.Submission;
import com.homework.entity.User;
import com.homework.service.AdminService;
import com.homework.service.CourseService;
import com.homework.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师控制器 - 发布作业、批改打分、查看提交列表、完成统计、
 * 课程查询、学生名单、成绩统计、班级通知发布
 * 所有接口前缀：/api/teacher
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    // 注入教师服务
    @Autowired
    private TeacherService teacherService;

    // 注入课程服务
    @Autowired
    private CourseService courseService;

    // 注入管理员服务（用于发布班级通知）
    @Autowired
    private AdminService adminService;

    /**
     * 发布作业
     */
    @PostMapping("/homework/publish")
    public Result<Void> publishHomework(@RequestBody HomeworkDTO dto, HttpServletRequest request) {
        // 从请求域中获取当前教师ID
        Long teacherId = (Long) request.getAttribute("userId");
        // 调用教师服务发布作业，传入作业信息和教师ID
        teacherService.publishHomework(dto, teacherId);
        // 返回发布成功
        return Result.success("发布成功", null);
    }

    /**
     * 查看我的作业列表（分页）
     */
    @GetMapping("/homework/list")
    public Result<PageResult<Homework>> homeworkList(
            @RequestParam(defaultValue = "1") int pageNum,   // 当前页码
            @RequestParam(defaultValue = "10") int pageSize, // 每页条数
            HttpServletRequest request) {
        // 从请求域中获取当前教师ID
        Long teacherId = (Long) request.getAttribute("userId");
        // 分页查询当前教师发布的作业列表
        List<Homework> list = teacherService.getMyHomeworkList(teacherId, pageNum, pageSize);
        // 查询当前教师发布的作业总数
        int total = teacherService.getMyHomeworkCount(teacherId);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 查看某作业的提交列表
     */
    @GetMapping("/homework/submissions/{homeworkId}")
    public Result<List<Submission>> homeworkSubmissions(@PathVariable Long homeworkId) {
        // 调用教师服务查询某份作业的所有学生提交记录
        return Result.success(teacherService.getSubmissionsByHomeworkId(homeworkId));
    }

    /**
     * 批改作业（打分评语，支持 is_returned 打回、is_excellent 优秀标记）
     */
    @PutMapping("/homework/grade")
    public Result<Void> gradeHomework(@RequestBody GradeDTO dto) {
        // 调用教师服务批改作业，传入成绩、评语等信息
        teacherService.gradeSubmission(dto);
        // 返回批改成功
        return Result.success("批改成功", null);
    }

    /**
     * 查看我的班级列表
     */
    @GetMapping("/classes")
    public Result<List<Clazz>> myClasses(HttpServletRequest request) {
        // 从请求域中获取当前教师ID
        Long teacherId = (Long) request.getAttribute("userId");
        // 调用教师服务查询当前教师负责的班级列表
        return Result.success(teacherService.getMyClasses(teacherId));
    }

    /**
     * 查看某作业的完成统计（已交人数、未交人数）
     */
    @GetMapping("/homework/statistics/{homeworkId}")
    public Result<Map<String, Object>> homeworkStatistics(@PathVariable Long homeworkId) {
        // 调用教师服务获取某份作业的提交统计（已交/未交人数）
        return Result.success(teacherService.getHomeworkStatistics(homeworkId));
    }

    // ==================== 课程相关 ====================

    /**
     * 查看我的课程列表（当前教师教授的课程）
     */
    @GetMapping("/course/list")
    public Result<List<Course>> myCourseList(HttpServletRequest request) {
        // 从请求域中获取当前教师ID
        Long teacherId = (Long) request.getAttribute("userId");
        // 调用课程服务查询当前教师教授的所有课程
        return Result.success(courseService.getCoursesByTeacherId(teacherId));
    }

    /**
     * 查看某课程的学生名单（通过课程关联的班级查询学生）
     */
    @GetMapping("/course/students/{courseId}")
    public Result<List<User>> courseStudents(@PathVariable Long courseId) {
        // 调用教师服务查询某门课程的所有学生名单
        return Result.success(teacherService.getCourseStudents(courseId));
    }

    // ==================== 成绩统计 ====================

    /**
     * 查看某作业的成绩统计（分数分布：优秀/良好/中等/及格/不及格）
     */
    @GetMapping("/grade/statistics/{homeworkId}")
    public Result<Map<String, Object>> gradeStatistics(@PathVariable Long homeworkId) {
        // 调用教师服务获取某份作业的成绩分布统计
        return Result.success(teacherService.getGradeStatistics(homeworkId));
    }

    // ==================== 班级通知发布 ====================

    /**
     * 发布班级通知（教师发布，announce_type 为 CLASS）
     */
    @PostMapping("/announcement/add")
    public Result<Void> addAnnouncement(@RequestBody Announcement announcement, HttpServletRequest request) {
        // 从请求域中获取当前发布者（教师）的用户ID
        Long publisherId = (Long) request.getAttribute("userId");
        // 班级通知默认设置类型为 CLASS
        if (announcement.getAnnounceType() == null || announcement.getAnnounceType().trim().isEmpty()) {
            // 如果公告类型为空，默认设为班级通知
            announcement.setAnnounceType("CLASS");
        }
        // 调用管理员服务发布公告
        adminService.addAnnouncement(announcement, publisherId);
        // 返回发布成功
        return Result.success("发布成功", null);
    }
}
