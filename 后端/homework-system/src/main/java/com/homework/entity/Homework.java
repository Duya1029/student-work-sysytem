package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 作业实体类 - 对应 sys_homework 表
 */
public class Homework {
    /** 作业ID */
    private Long homeworkId; // 作业ID，主键
    /** 作业标题 */
    private String title; // 作业标题
    /** 作业描述/要求 */
    private String description; // 作业描述/要求
    /** 发布教师ID */
    private Long teacherId; // 发布教师ID
    /** 教师姓名（关联查询） */
    private String teacherName; // 教师姓名，关联查询字段
    /** 班级ID */
    private Long classId; // 班级ID
    /** 班级名称（关联查询） */
    private String className; // 班级名称，关联查询字段
    /** 截止时间 */
    private LocalDateTime deadline; // 截止时间
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间
    /** 附件（文件路径） */
    private String attachment; // 附件文件路径
    /** 所属课程ID */
    private Long courseId; // 所属课程ID
    /** 所属课程名称（关联查询） */
    private String courseName; // 所属课程名称，关联查询字段

    public Long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

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
}
