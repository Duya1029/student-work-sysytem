package com.homework.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类 - 对应 sys_course 表（通过 sys_course_class 关联多班级）
 */
public class Course {
    /** 课程ID */
    private Long courseId; // 课程ID，主键
    /** 课程名称 */
    private String courseName; // 课程名称
    /** 课程代码 */
    private String courseCode; // 课程代码
    /** 学分 */
    private BigDecimal credits; // 学分
    /** 学时 */
    private Integer hours; // 学时
    /** 任课教师ID */
    private Long teacherId; // 任课教师ID
    /** 任课教师姓名（关联查询） */
    private String teacherName; // 任课教师姓名，关联查询字段
    /** 所属学期ID */
    private Long semesterId; // 所属学期ID
    /** 所属学期名称（关联查询） */
    private String semesterName; // 所属学期名称，关联查询字段
    /** 关联班级ID列表（关联查询，逗号分隔字符串） */
    private String classIds; // 关联班级ID列表，逗号分隔字符串
    /** 关联班级名称列表（关联查询，逗号分隔字符串） */
    private String classNames; // 关联班级名称列表，逗号分隔字符串
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getClassIds() {
        return classIds;
    }

    public void setClassIds(String classIds) {
        this.classIds = classIds;
    }

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
