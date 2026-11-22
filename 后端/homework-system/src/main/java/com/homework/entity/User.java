package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类 - 对应 sys_user 表
 * 角色：ADMIN(管理员) / TEACHER(教师) / STUDENT(学生)
 */
public class User {
    /** 用户ID */
    private Long userId; // 用户ID，主键
    /** 用户名（登录账号） */
    private String username; // 用户名，用于登录
    /** 密码 */
    private String password; // 密码，加密存储
    /** 真实姓名 */
    private String realName; // 真实姓名
    /** 角色：ADMIN/TEACHER/STUDENT */
    private String role; // 角色：ADMIN(管理员)/TEACHER(教师)/STUDENT(学生)
    /** 班级ID（学生必填） */
    private Long classId; // 班级ID，学生必填
    /** 学号（学生专用） */
    private String studentNo; // 学号，学生专用
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间
    /** 班级名称（关联查询） */
    private String className; // 班级名称，关联查询字段

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
