package com.homework.service.impl;

import com.homework.entity.Homework;
import com.homework.mapper.HomeworkMapper;
import com.homework.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作业服务实现类（管理员视角）
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper; // 作业数据访问对象，用于操作作业表

    @Override
    public List<Homework> getAllHomeworkList(int pageNum, int pageSize, String className, String teacherName) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return homeworkMapper.selectListForAdmin(offset, pageSize, className, teacherName); // 调用Mapper分页查询所有作业列表（管理员视角，可按班级和教师筛选）
    }

    @Override
    public int getAllHomeworkCount(String className, String teacherName) {
        return homeworkMapper.selectCountForAdmin(className, teacherName); // 调用Mapper查询符合条件的作业总数
    }

    @Override
    public void deleteHomework(Long homeworkId) {
        if (homeworkId == null) { // 校验作业ID是否为空
            throw new IllegalArgumentException("作业ID不能为空"); // ID为空则抛出异常
        }
        homeworkMapper.deleteById(homeworkId); // 调用Mapper根据ID删除作业记录
    }
}
