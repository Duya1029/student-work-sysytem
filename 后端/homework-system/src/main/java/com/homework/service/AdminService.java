package com.homework.service;

import com.homework.entity.Announcement;
import com.homework.entity.Clazz;

import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口（班级管理、账号管理、作业记录查询、公告管理、统计数据）
 */
public interface AdminService {

    // ==================== 班级管理 ====================

    /**
     * 分页查询班级列表
     */
    // 分页查询班级列表，支持按班级名称模糊查询
    List<Clazz> getClassList(int pageNum, int pageSize, String className);

    /**
     * 查询班级总数
     */
    // 查询符合条件的班级总记录数，用于分页
    int getClassCount(String className);

    /**
     * 查询所有班级（下拉列表用）
     */
    // 查询全部班级，用于下拉列表选择
    List<Clazz> getAllClasses();

    /**
     * 新增班级
     */
    // 新增一条班级记录
    void addClass(String className);

    /**
     * 修改班级
     */
    // 根据班级ID修改班级名称
    void updateClass(Long classId, String className);

    /**
     * 删除班级
     */
    // 根据班级ID删除班级
    void deleteClass(Long classId);

    // ==================== 账号管理 ====================

    /**
     * 分页查询用户列表
     */
    // 分页查询用户列表，支持按角色、真实姓名、班级ID筛选
    List<com.homework.entity.User> getUserList(int pageNum, int pageSize, String role, String realName, Long classId);

    /**
     * 查询用户总数
     */
    // 查询符合条件的用户总记录数，用于分页
    int getUserCount(String role, String realName, Long classId);

    /**
     * 新增用户
     */
    // 新增用户账号，包含学生或教师信息
    void addUser(com.homework.entity.User user);

    /**
     * 修改用户
     */
    // 修改用户信息
    void updateUser(com.homework.entity.User user);

    /**
     * 删除用户
     */
    // 根据用户ID删除用户
    void deleteUser(Long userId);

    /**
     * 重置密码
     */
    // 重置指定用户的密码为新密码
    void resetPassword(Long userId, String newPassword);

    /**
     * 查询所有教师
     */
    // 查询所有教师角色的用户，用于下拉选择
    List<com.homework.entity.User> getTeachers();

    // ==================== 公告管理 ====================

    /**
     * 查询全部公告
     */
    // 查询系统全部公告列表
    List<Announcement> getAnnouncementList();

    /**
     * 新增公告（发布人为当前登录管理员）
     */
    // 新增公告，发布人为当前登录的管理员
    void addAnnouncement(Announcement announcement, Long publisherId);

    /**
     * 修改公告
     */
    // 修改公告内容
    void updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     */
    // 根据公告ID删除公告
    void deleteAnnouncement(Long id);

    // ==================== 统计数据 ====================

    /**
     * 获取系统统计数据（学生数、教师数、班级数、作业数、课程数、学期数、日志数）
     */
    // 获取系统综合统计数据，包括学生数、教师数、班级数、作业数、课程数、学期数、日志数等
    Map<String, Object> getStatistics();
}
