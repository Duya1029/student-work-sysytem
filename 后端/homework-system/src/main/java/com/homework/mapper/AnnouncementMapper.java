package com.homework.mapper;

import com.homework.entity.Announcement;

import java.util.List;

/**
 * 公告 Mapper 接口 - 对应 AnnouncementMapper.xml
 */
public interface AnnouncementMapper {

    // 查询全部公告（关联发布人姓名）
    List<Announcement> getAllAnnouncements();

    // 根据ID查询公告
    Announcement getAnnouncementById(Long id);

    // 新增公告
    void insertAnnouncement(Announcement announcement);

    // 修改公告
    void updateAnnouncement(Announcement announcement);

    // 删除公告
    void deleteAnnouncement(Long id);
}
