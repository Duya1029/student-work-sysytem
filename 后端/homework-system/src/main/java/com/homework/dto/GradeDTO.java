package com.homework.dto;

/**
 * 批改作业请求 DTO
 */
public class GradeDTO {
    // 提交记录ID
    private Long submissionId;
    // 分数（0-100）
    private Integer score;
    // 教师评语
    private String comment;
    // 是否打回重做
    private Boolean isReturned;
    // 是否标记为优秀作业
    private Boolean isExcellent;

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
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
