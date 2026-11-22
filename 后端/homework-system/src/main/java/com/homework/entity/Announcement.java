package com.homework.entity;

import java.time.LocalDateTime;

/**
 * 公告实体类 - 对应 sys_announcement 表
 */
public class Announcement {
    /** 公告ID */
    private Long announceId; // 公告ID，主键
    /** 公告标题 */
    private String title; // 公告标题
    /** 公告内容 */
    private String content; // 公告内容
    /** 发布人ID */
    private Long publisherId; // 发布人ID
    /** 发布人姓名（关联查询） */
    private String publisherName; // 发布人姓名，关联查询字段
    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间
    /** 公告类型：SYSTEM-系统公告，CLASS-班级通知 */
    private String announceType; // 公告类型：SYSTEM-系统公告，CLASS-班级通知
    /** 关联班级ID（班级通知专用） */
    private Long classId; // 关联班级ID，班级通知专用
    /** 是否置顶：true-置顶，false-普通 */
    private Boolean isPinned; // 是否置顶：true-置顶，false-普通

    public Long getAnnounceId() {
        return announceId;
    }

    public void setAnnounceId(Long announceId) {
        this.announceId = announceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getAnnounceType() {
        return announceType;
    }

    public void setAnnounceType(String announceType) {
        this.announceType = announceType;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }
}
