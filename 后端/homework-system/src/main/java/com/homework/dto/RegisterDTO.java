package com.homework.dto;

/**
 * 注册请求 DTO
 * 学生/教师自助注册时提交的信息
 */
public class RegisterDTO {
    // 登录账号
    private String username;
    // 登录密码
    private String password;
    // 真实姓名
    private String realName;
    // 注册角色：TEACHER(教师) / STUDENT(学生)
    private String role;
    // 班级ID（学生必填）
    private Long classId;
    // 学号（学生选填）
    private String studentNo;

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
}
