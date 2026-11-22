package com.homework.service.impl;

import com.homework.entity.Announcement;
import com.homework.entity.Clazz;
import com.homework.entity.User;
import com.homework.mapper.AnnouncementMapper;
import com.homework.mapper.ClassMapper;
import com.homework.mapper.CourseMapper;
import com.homework.mapper.HomeworkMapper;
import com.homework.mapper.SemesterMapper;
import com.homework.mapper.SystemLogMapper;
import com.homework.mapper.UserMapper;
import com.homework.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ClassMapper classMapper; // 班级数据访问对象，用于操作班级表

    @Autowired
    private UserMapper userMapper; // 用户数据访问对象，用于操作用户表

    @Autowired
    private AnnouncementMapper announcementMapper; // 公告数据访问对象，用于操作公告表

    @Autowired
    private HomeworkMapper homeworkMapper; // 作业数据访问对象，用于操作作业表

    @Autowired
    private CourseMapper courseMapper; // 课程数据访问对象，用于操作课程表

    @Autowired
    private SemesterMapper semesterMapper; // 学期数据访问对象，用于操作学期表

    @Autowired
    private SystemLogMapper systemLogMapper; // 系统日志数据访问对象，用于操作日志表

    // ==================== 班级管理 ====================

    @Override
    public List<Clazz> getClassList(int pageNum, int pageSize, String className) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return classMapper.selectList(offset, pageSize, className); // 调用Mapper分页查询班级列表并返回
    }

    @Override
    public int getClassCount(String className) {
        return classMapper.selectCount(className); // 调用Mapper查询符合条件的班级总数并返回
    }

    @Override
    public List<Clazz> getAllClasses() {
        return classMapper.selectAll(); // 调用Mapper查询所有班级并返回列表
    }

    @Override
    public void addClass(String className) {
        if (className == null || className.trim().isEmpty()) { // 校验班级名称是否为空
            throw new IllegalArgumentException("班级名称不能为空"); // 名称为空则抛出异常
        }
        Clazz clazz = new Clazz(); // 创建班级实体对象
        clazz.setClassName(className.trim()); // 设置去除空格后的班级名称
        classMapper.insert(clazz); // 调用Mapper将班级信息插入数据库
    }

    @Override
    public void updateClass(Long classId, String className) {
        if (classId == null) { // 校验班级ID是否为空
            throw new IllegalArgumentException("班级ID不能为空"); // ID为空则抛出异常
        }
        if (className == null || className.trim().isEmpty()) { // 校验班级名称是否为空
            throw new IllegalArgumentException("班级名称不能为空"); // 名称为空则抛出异常
        }
        Clazz clazz = new Clazz(); // 创建班级实体对象
        clazz.setClassId(classId); // 设置要更新的班级ID
        clazz.setClassName(className.trim()); // 设置去除空格后的班级名称
        classMapper.update(clazz); // 调用Mapper更新班级信息
    }

    @Override
    public void deleteClass(Long classId) {
        if (classId == null) { // 校验班级ID是否为空
            throw new IllegalArgumentException("班级ID不能为空"); // ID为空则抛出异常
        }
        classMapper.deleteById(classId); // 调用Mapper根据ID删除班级记录
    }

    // ==================== 账号管理 ====================

    @Override
    public List<User> getUserList(int pageNum, int pageSize, String role, String realName, Long classId) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return userMapper.selectList(offset, pageSize, role, realName, classId); // 调用Mapper分页查询用户列表并返回
    }

    @Override
    public int getUserCount(String role, String realName, Long classId) {
        return userMapper.selectCount(role, realName, classId); // 调用Mapper查询符合条件的用户总数并返回
    }

    @Override
    public void addUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) { // 校验用户名是否为空
            throw new IllegalArgumentException("用户名不能为空"); // 用户名为空则抛出异常
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) { // 校验密码是否为空
            throw new IllegalArgumentException("密码不能为空"); // 密码为空则抛出异常
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) { // 校验真实姓名是否为空
            throw new IllegalArgumentException("真实姓名不能为空"); // 真实姓名为空则抛出异常
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) { // 校验角色是否为空
            throw new IllegalArgumentException("角色不能为空"); // 角色为空则抛出异常
        }
        // 学生必须选择班级
        if ("STUDENT".equals(user.getRole()) && user.getClassId() == null) { // 判断是否为学生且未选择班级
            throw new IllegalArgumentException("学生必须选择班级"); // 学生未选班级则抛出异常
        }
        userMapper.insert(user); // 调用Mapper将用户信息插入数据库
    }

    @Override
    public void updateUser(User user) {
        if (user.getUserId() == null) { // 校验用户ID是否为空
            throw new IllegalArgumentException("用户ID不能为空"); // ID为空则抛出异常
        }
        // 密码为空则不修改密码
        if (user.getPassword() != null && user.getPassword().trim().isEmpty()) { // 判断密码是否为空字符串
            user.setPassword(null); // 将密码置为null，表示不修改密码
        }
        userMapper.update(user); // 调用Mapper更新用户信息
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) { // 校验用户ID是否为空
            throw new IllegalArgumentException("用户ID不能为空"); // ID为空则抛出异常
        }
        userMapper.deleteById(userId); // 调用Mapper根据ID删除用户记录
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userId == null) { // 校验用户ID是否为空
            throw new IllegalArgumentException("用户ID不能为空"); // ID为空则抛出异常
        }
        if (newPassword == null || newPassword.trim().isEmpty()) { // 校验新密码是否为空
            throw new IllegalArgumentException("新密码不能为空"); // 新密码为空则抛出异常
        }
        userMapper.updatePassword(userId, newPassword); // 调用Mapper更新用户密码
    }

    @Override
    public List<User> getTeachers() {
        return userMapper.selectTeachers(); // 调用Mapper查询所有教师列表并返回
    }

    // ==================== 公告管理 ====================

    @Override
    public List<Announcement> getAnnouncementList() {
        return announcementMapper.getAllAnnouncements(); // 调用Mapper查询所有公告列表并返回
    }

    @Override
    public void addAnnouncement(Announcement announcement, Long publisherId) {
        if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) { // 校验公告标题是否为空
            throw new IllegalArgumentException("公告标题不能为空"); // 标题为空则抛出异常
        }
        if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) { // 校验公告内容是否为空
            throw new IllegalArgumentException("公告内容不能为空"); // 内容为空则抛出异常
        }
        announcement.setTitle(announcement.getTitle().trim()); // 设置去除空格后的公告标题
        announcement.setContent(announcement.getContent().trim()); // 设置去除空格后的公告内容
        announcement.setPublisherId(publisherId); // 设置发布者ID
        announcement.setCreateTime(LocalDateTime.now()); // 设置公告创建时间为当前时间
        // 班级通知必须设置默认类型
        if (announcement.getAnnounceType() == null || announcement.getAnnounceType().trim().isEmpty()) { // 判断公告类型是否为空
            announcement.setAnnounceType("SYSTEM"); // 设置默认公告类型为系统公告
        }
        if (announcement.getIsPinned() == null) { // 判断置顶状态是否为空
            announcement.setIsPinned(false); // 设置默认不置顶
        }
        announcementMapper.insertAnnouncement(announcement); // 调用Mapper将公告信息插入数据库
    }

    @Override
    public void updateAnnouncement(Announcement announcement) {
        if (announcement.getAnnounceId() == null) { // 校验公告ID是否为空
            throw new IllegalArgumentException("公告ID不能为空"); // ID为空则抛出异常
        }
        announcementMapper.updateAnnouncement(announcement); // 调用Mapper更新公告信息
    }

    @Override
    public void deleteAnnouncement(Long id) {
        if (id == null) { // 校验公告ID是否为空
            throw new IllegalArgumentException("公告ID不能为空"); // ID为空则抛出异常
        }
        announcementMapper.deleteAnnouncement(id); // 调用Mapper根据ID删除公告记录
    }

    // ==================== 统计数据 ====================

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>(); // 创建统计结果Map集合
        stats.put("studentCount", userMapper.selectCount("STUDENT", null, null)); // 统计学生总数并放入Map
        stats.put("teacherCount", userMapper.selectCount("TEACHER", null, null)); // 统计教师总数并放入Map
        stats.put("classCount", classMapper.selectCount(null)); // 统计班级总数并放入Map
        stats.put("homeworkCount", homeworkMapper.selectCountForAdmin(null, null)); // 统计作业总数并放入Map
        // 扩展统计：课程数、学期数、日志数
        stats.put("courseCount", courseMapper.selectCount(null, null, null)); // 统计课程总数并放入Map
        stats.put("semesterCount", semesterMapper.selectAll().size()); // 统计学期总数并放入Map
        stats.put("logCount", systemLogMapper.selectCount(null, null)); // 统计系统日志总数并放入Map
        return stats; // 返回包含各项统计数据的Map集合
    }
}
