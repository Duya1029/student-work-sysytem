package com.homework.dto;

/**
 * 发布作业请求 DTO
 */
public class HomeworkDTO {
    // 作业ID（修改时必填）
    private Long homeworkId;
    // 作业标题
    private String title;
    // 作业描述
    private String description;
    // 班级ID
    private Long classId;
    // 截止时间（格式：yyyy-MM-ddTHH:mm:ss）
    private String deadline;

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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
