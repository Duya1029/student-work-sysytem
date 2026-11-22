package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 作业提交实体类 - 对应 sys_submission 表
 */
public class Submission {
    /** 提交ID */
    private Long submissionId; // 提交ID，主键
    /** 作业ID */
    private Long homeworkId; // 作业ID，外键
    /** 学生ID */
    private Long studentId; // 学生ID，外键
    /** 学生姓名（关联查询） */
    private String studentName; // 学生姓名，关联查询字段
    /** 学号（关联查询） */
    private String studentNo; // 学号，关联查询字段
    /** 班级名称（关联查询） */
    private String className; // 班级名称，关联查询字段
    /** 提交内容 */
    private String content; // 提交内容
    /** 提交时间 */
    private LocalDateTime submitTime; // 提交时间
    /** 分数（0-100，未批改为null） */
    private Integer score; // 分数，0-100，未批改为null
    /** 教师评语 */
    private String comment; // 教师评语
    /** 批改时间 */
    private LocalDateTime gradeTime; // 批改时间
    /** 作业标题（关联查询） */
    private String homeworkTitle; // 作业标题，关联查询字段
    /** 附件（文件路径） */
    private String attachment; // 附件文件路径
    /** 是否已打回重做 */
    private Boolean isReturned; // 是否已打回重做
    /** 是否为优秀作业 */
    private Boolean isExcellent; // 是否为优秀作业

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getGradeTime() {
        return gradeTime;
    }

    public void setGradeTime(LocalDateTime gradeTime) {
        this.gradeTime = gradeTime;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }

    public Boolean getIsExcellent() {
        return isExcellent;
    }

    public void setIsExcellent(Boolean isExcellent) {
        this.isExcellent = isExcellent;
    }
}
