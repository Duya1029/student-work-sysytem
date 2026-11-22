package com.homework.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学期实体类 - 对应 sys_semester 表
 */
public class Semester {
    /** 学期ID */
    private Long semesterId; // 学期ID，主键
    /** 学期名称 */
    private String semesterName; // 学期名称
    /** 开始日期 */
    private LocalDate startDate; // 开始日期
    /** 结束日期 */
    private LocalDate endDate; // 结束日期
    /** 状态：ACTIVE-启用，INACTIVE-停用 */
    private String status; // 状态：ACTIVE-启用，INACTIVE-停用
    /** 是否为当前学期 */
    private Boolean isCurrent; // 是否为当前学期
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
