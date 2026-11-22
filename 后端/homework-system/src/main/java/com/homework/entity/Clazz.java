package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 班级实体类 - 对应 sys_class 表
 */
public class Clazz {
    /** 班级ID */
    private Long classId; // 班级ID，主键
    /** 班级名称 */
    private String className; // 班级名称
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间
    /** 学生人数（关联统计） */
    private Integer studentCount; // 学生人数，关联统计
    /** 班主任ID */
    private Long headTeacherId; // 班主任ID
    /** 班主任姓名（关联查询） */
    private String headTeacherName; // 班主任姓名，关联查询字段

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Long getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(Long headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public String getHeadTeacherName() {
        return headTeacherName;
    }

    public void setHeadTeacherName(String headTeacherName) {
        this.headTeacherName = headTeacherName;
    }
}
