package com.homework.controller;

import com.homework.common.Result;
import com.homework.dto.PageResult;
import com.homework.dto.ResetPwdDTO;
import com.homework.entity.Announcement;
import com.homework.entity.Clazz;
import com.homework.entity.Course;
import com.homework.entity.Homework;
import com.homework.entity.Semester;
import com.homework.entity.Submission;
import com.homework.entity.SystemLog;
import com.homework.entity.User;
import com.homework.service.AdminService;
import com.homework.service.CourseService;
import com.homework.service.HomeworkService;
import com.homework.service.SemesterService;
import com.homework.service.SystemLogService;
import com.homework.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器 - 班级管理、账号管理、作业记录查询、公告管理、统计数据、
 * 课程管理、学期管理、系统日志查询
 * 所有接口前缀：/api/admin
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // 注入管理员服务
    @Autowired
    private AdminService adminService;

    // 注入作业服务
    @Autowired
    private HomeworkService homeworkService;

    // 注入教师服务
    @Autowired
    private TeacherService teacherService;

    // 注入课程服务
    @Autowired
    private CourseService courseService;

    // 注入学期服务
    @Autowired
    private SemesterService semesterService;

    // 注入系统日志服务
    @Autowired
    private SystemLogService systemLogService;

    // ==================== 班级管理 ====================

    /**
     * 班级列表（分页）
     */
    @GetMapping("/class/list")
    public Result<PageResult<Clazz>> classList(
            @RequestParam(defaultValue = "1") int pageNum,          // 当前页码，默认1
            @RequestParam(defaultValue = "10") int pageSize,        // 每页条数，默认10
            @RequestParam(required = false) String className) {     // 班级名称（模糊查询，可选）
        // 调用管理员服务分页查询班级列表
        List<Clazz> list = adminService.getClassList(pageNum, pageSize, className);
        // 查询符合条件的班级总数
        int total = adminService.getClassCount(className);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 查询所有班级（下拉列表用）
     */
    @GetMapping("/class/all")
    public Result<List<Clazz>> allClasses() {
        // 查询全部班级，用于下拉选择
        return Result.success(adminService.getAllClasses());
    }

    /**
     * 新增班级
     */
    @PostMapping("/class/add")
    public Result<Void> addClass(@RequestBody Clazz clazz) {
        // 调用管理员服务新增班级，传入班级名称
        adminService.addClass(clazz.getClassName());
        // 返回新增成功
        return Result.success("新增成功", null);
    }

    /**
     * 修改班级
     */
    @PutMapping("/class/update")
    public Result<Void> updateClass(@RequestBody Clazz clazz) {
        // 调用管理员服务修改班级，传入班级ID和新名称
        adminService.updateClass(clazz.getClassId(), clazz.getClassName());
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/class/delete/{classId}")
    public Result<Void> deleteClass(@PathVariable Long classId) {
        // 调用管理员服务根据班级ID删除班级
        adminService.deleteClass(classId);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    // ==================== 账号管理 ====================

    /**
     * 账号列表（分页）
     */
    @GetMapping("/user/list")
    public Result<PageResult<User>> userList(
            @RequestParam(defaultValue = "1") int pageNum,          // 当前页码
            @RequestParam(defaultValue = "10") int pageSize,        // 每页条数
            @RequestParam(required = false) String role,            // 角色筛选（ADMIN/TEACHER/STUDENT），可选
            @RequestParam(required = false) String realName,        // 姓名模糊查询，可选
            @RequestParam(required = false) Long classId) {         // 班级ID筛选，可选
        // 分页查询用户列表
        List<User> list = adminService.getUserList(pageNum, pageSize, role, realName, classId);
        // 查询符合条件的用户总数
        int total = adminService.getUserCount(role, realName, classId);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 新增账号
     */
    @PostMapping("/user/add")
    public Result<Void> addUser(@RequestBody User user) {
        // 调用管理员服务新增用户
        adminService.addUser(user);
        // 返回新增成功
        return Result.success("新增成功", null);
    }

    /**
     * 修改账号
     */
    @PutMapping("/user/update")
    public Result<Void> updateUser(@RequestBody User user) {
        // 调用管理员服务修改用户信息
        adminService.updateUser(user);
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 删除账号
     */
    @DeleteMapping("/user/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        // 调用管理员服务根据用户ID删除用户
        adminService.deleteUser(userId);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    /**
     * 重置密码
     */
    @PutMapping("/user/resetPwd")
    public Result<Void> resetPassword(@RequestBody ResetPwdDTO dto) {
        // 调用管理员服务重置指定用户的密码
        adminService.resetPassword(dto.getUserId(), dto.getNewPassword());
        // 返回重置成功
        return Result.success("重置成功", null);
    }

    /**
     * 查询所有教师（下拉列表用）
     */
    @GetMapping("/teacher/list")
    public Result<List<User>> teacherList() {
        // 查询所有教师，用于下拉选择（如分配班主任）
        return Result.success(adminService.getTeachers());
    }

    // ==================== 作业记录查询 ====================

    /**
     * 查看全部作业记录（分页）
     */
    @GetMapping("/homework/list")
    public Result<PageResult<Homework>> homeworkList(
            @RequestParam(defaultValue = "1") int pageNum,          // 当前页码
            @RequestParam(defaultValue = "10") int pageSize,        // 每页条数
            @RequestParam(required = false) String className,       // 班级名称筛选，可选
            @RequestParam(required = false) String teacherName) {   // 教师姓名筛选，可选
        // 分页查询所有作业记录
        List<Homework> list = homeworkService.getAllHomeworkList(pageNum, pageSize, className, teacherName);
        // 查询符合条件的作业总数
        int total = homeworkService.getAllHomeworkCount(className, teacherName);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 查看作业提交详情
     */
    @GetMapping("/homework/submissions/{homeworkId}")
    public Result<List<Submission>> homeworkSubmissions(@PathVariable Long homeworkId) {
        // 调用教师服务查询某份作业的所有提交记录
        return Result.success(teacherService.getSubmissionsByHomeworkId(homeworkId));
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/homework/delete/{homeworkId}")
    public Result<Void> deleteHomework(@PathVariable Long homeworkId) {
        // 调用作业服务根据作业ID删除作业
        homeworkService.deleteHomework(homeworkId);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    // ==================== 公告管理 ====================

    /**
     * 发布公告（支持 announce_type, class_id, is_pinned 字段）
     */
    @PostMapping("/announcement/add")
    public Result<Void> addAnnouncement(@RequestBody Announcement announcement, HttpServletRequest request) {
        // 从请求域中获取当前发布者（管理员）的用户ID
        Long publisherId = (Long) request.getAttribute("userId");
        // 调用管理员服务发布公告，传入公告内容和发布者ID
        adminService.addAnnouncement(announcement, publisherId);
        // 返回发布成功
        return Result.success("发布成功", null);
    }

    /**
     * 修改公告（支持 announce_type, class_id, is_pinned 字段）
     */
    @PutMapping("/announcement/update")
    public Result<Void> updateAnnouncement(@RequestBody Announcement announcement) {
        // 调用管理员服务修改公告
        adminService.updateAnnouncement(announcement);
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/announcement/delete/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        // 调用管理员服务根据公告ID删除公告
        adminService.deleteAnnouncement(id);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    // ==================== 课程管理 ====================

    /**
     * 课程列表（分页，支持按课程名、教师、学期筛选）
     */
    @GetMapping("/course/list")
    public Result<PageResult<Course>> courseList(
            @RequestParam(defaultValue = "1") int pageNum,          // 当前页码
            @RequestParam(defaultValue = "10") int pageSize,        // 每页条数
            @RequestParam(required = false) String courseName,      // 课程名称模糊查询，可选
            @RequestParam(required = false) Long teacherId,         // 教师ID筛选，可选
            @RequestParam(required = false) Long semesterId) {      // 学期ID筛选，可选
        // 分页查询课程列表
        List<Course> list = courseService.getCourseList(pageNum, pageSize, courseName, teacherId, semesterId);
        // 查询符合条件的课程总数
        int total = courseService.getCourseCount(courseName, teacherId, semesterId);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    /**
     * 新增课程（支持关联多个班级，classIds 为逗号分隔的班级ID字符串）
     */
    @PostMapping("/course/add")
    public Result<Void> addCourse(@RequestBody Course course) {
        // 调用课程服务新增课程
        courseService.addCourse(course);
        // 返回新增成功
        return Result.success("新增成功", null);
    }

    /**
     * 修改课程（支持更新关联班级）
     */
    @PutMapping("/course/update")
    public Result<Void> updateCourse(@RequestBody Course course) {
        // 调用课程服务修改课程信息
        courseService.updateCourse(course);
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/course/delete/{courseId}")
    public Result<Void> deleteCourse(@PathVariable Long courseId) {
        // 调用课程服务根据课程ID删除课程
        courseService.deleteCourse(courseId);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    // ==================== 学期管理 ====================

    /**
     * 学期列表
     */
    @GetMapping("/semester/list")
    public Result<List<Semester>> semesterList() {
        // 调用学期服务查询所有学期
        return Result.success(semesterService.getAllSemesters());
    }

    /**
     * 新增学期
     */
    @PostMapping("/semester/add")
    public Result<Void> addSemester(@RequestBody Semester semester) {
        // 调用学期服务新增学期
        semesterService.addSemester(semester);
        // 返回新增成功
        return Result.success("新增成功", null);
    }

    /**
     * 修改学期
     */
    @PutMapping("/semester/update")
    public Result<Void> updateSemester(@RequestBody Semester semester) {
        // 调用学期服务修改学期信息
        semesterService.updateSemester(semester);
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 删除学期
     */
    @DeleteMapping("/semester/delete/{semesterId}")
    public Result<Void> deleteSemester(@PathVariable Long semesterId) {
        // 调用学期服务根据学期ID删除学期
        semesterService.deleteSemester(semesterId);
        // 返回删除成功
        return Result.success("删除成功", null);
    }

    /**
     * 设置当前学期
     */
    @PutMapping("/semester/set-current/{semesterId}")
    public Result<Void> setCurrentSemester(@PathVariable Long semesterId) {
        // 调用学期服务将指定学期设为当前学期
        semesterService.setCurrentSemester(semesterId);
        // 返回设置成功
        return Result.success("设置成功", null);
    }

    // ==================== 系统日志查询 ====================

    /**
     * 系统日志列表（分页，支持按用户名、日志类型筛选）
     */
    @GetMapping("/log/list")
    public Result<PageResult<SystemLog>> logList(
            @RequestParam(defaultValue = "1") int pageNum,          // 当前页码
            @RequestParam(defaultValue = "10") int pageSize,        // 每页条数
            @RequestParam(required = false) String username,        // 用户名筛选，可选
            @RequestParam(required = false) String logType) {       // 日志类型筛选，可选
        // 分页查询系统日志列表
        List<SystemLog> list = systemLogService.getLogList(pageNum, pageSize, username, logType);
        // 查询符合条件的日志总数
        int total = systemLogService.getLogCount(username, logType);
        // 封装分页结果返回
        return Result.success(new PageResult<>((long) total, list, pageNum, pageSize));
    }

    // ==================== 统计数据 ====================

    /**
     * 获取系统统计数据（学生数、教师数、班级数、作业数、课程数、学期数、日志数）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        // 调用管理员服务获取系统各项统计数据
        return Result.success(adminService.getStatistics());
    }
}
