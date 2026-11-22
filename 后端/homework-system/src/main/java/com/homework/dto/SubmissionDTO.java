package com.homework.dto;

/**
 * 提交作业请求 DTO
 */
public class SubmissionDTO {
    // 作业ID
    private Long homeworkId;
    // 提交内容
    private String content;

    public Long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
