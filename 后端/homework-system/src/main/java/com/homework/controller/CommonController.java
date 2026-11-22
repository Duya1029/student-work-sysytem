package com.homework.controller;

import com.homework.common.Constants;
import com.homework.common.Result;
import com.homework.dto.LoginDTO;
import com.homework.dto.LoginVO;
import com.homework.dto.RegisterDTO;
import com.homework.entity.Announcement;
import com.homework.entity.Clazz;
import com.homework.entity.Semester;
import com.homework.entity.User;
import com.homework.mapper.ClassMapper;
import com.homework.service.AdminService;
import com.homework.service.SemesterService;
import com.homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公共控制器 - 登录、退出、获取当前用户信息、修改密码、公告列表、当前学期
 */
@RestController
@RequestMapping("/api")
public class CommonController {

    // 注入用户服务，处理用户相关业务
    @Autowired
    private UserService userService;

    // 注入管理员服务，处理公告等业务
    @Autowired
    private AdminService adminService;

    // 注入学期服务，处理学期相关业务
    @Autowired
    private SemesterService semesterService;

    // 注入班级 Mapper，用于注册页面获取班级下拉列表
    @Autowired
    private ClassMapper classMapper;

    /**
     * 自助注册接口（学生/教师）
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO registerDTO) {
        // 调用用户服务完成注册，注册成功后直接返回登录信息（自动登录）
        LoginVO vo = userService.register(registerDTO);
        // 返回注册成功结果
        return Result.success("注册成功", vo);
    }

    /**
     * 注册页面获取班级列表（无需登录）
     */
    @GetMapping("/register/classes")
    public Result<List<Clazz>> registerClasses() {
        // 查询所有班级，供学生注册时选择
        return Result.success(classMapper.selectAll());
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 调用用户服务的登录方法，传入登录参数（账号、密码）
        LoginVO vo = userService.login(loginDTO);
        // 返回登录成功结果，包含 Token 和用户信息
        return Result.success("登录成功", vo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，客户端清除 Token 即可
        return Result.success("退出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        // 从请求域中获取当前登录用户ID（由JWT拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        // 根据用户ID查询用户完整信息
        User user = userService.getUserById(userId);
        // 不返回密码，保证安全
        if (user != null) {
            // 将密码字段置空，避免泄露
            user.setPassword(null);
        }
        // 返回用户信息
        return Result.success(user);
    }

    /**
     * 修改密码（需验证原密码）
     */
    @PutMapping("/user/changePwd")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        // 从请求域中获取当前登录用户名
        String username = (String) request.getAttribute("username");
        // 从请求体中获取原密码
        String oldPassword = body.get("oldPassword");
        // 从请求体中获取新密码
        String newPassword = body.get("newPassword");
        // 调用用户服务修改密码，需验证原密码是否正确
        userService.changePassword(username, oldPassword, newPassword);
        // 返回修改成功
        return Result.success("修改成功", null);
    }

    /**
     * 获取公告列表（所有登录用户可见，按置顶优先、时间倒序排列）
     */
    @GetMapping("/announcement/list")
    public Result<List<Announcement>> announcementList() {
        // 调用管理员服务获取公告列表
        return Result.success(adminService.getAnnouncementList());
    }

    /**
     * 获取当前学期信息
     */
    @GetMapping("/semester/current")
    public Result<Semester> currentSemester() {
        // 调用学期服务获取当前学期
        Semester semester = semesterService.getCurrentSemester();
        // 返回当前学期信息
        return Result.success(semester);
    }
}
