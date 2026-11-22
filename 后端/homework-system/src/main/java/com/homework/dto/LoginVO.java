package com.homework.dto;

/**
 * 登录响应 DTO - 返回用户信息和 Token
 */
public class LoginVO {
    // 用户ID
    private Long userId;
    // 用户名
    private String username;
    // 真实姓名
    private String realName;
    // 角色
    private String role;
    // 班级ID
    private Long classId;
    // 班级名称
    private String className;
    // JWT Token
    private String token;

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
