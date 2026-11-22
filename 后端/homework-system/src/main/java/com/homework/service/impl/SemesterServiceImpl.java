package com.homework.service.impl;

import com.homework.entity.Semester;
import com.homework.mapper.SemesterMapper;
import com.homework.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学期服务实现类
 */
@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterMapper semesterMapper; // 学期数据访问对象，用于操作学期表

    @Override
    public List<Semester> getAllSemesters() {
        return semesterMapper.selectAll(); // 调用Mapper查询所有学期列表
    }

    @Override
    public Semester getSemesterById(Long semesterId) {
        if (semesterId == null) { // 校验学期ID是否为空
            throw new IllegalArgumentException("学期ID不能为空"); // ID为空则抛出异常
        }
        return semesterMapper.selectById(semesterId); // 调用Mapper根据ID查询学期详情
    }

    @Override
    public Semester getCurrentSemester() {
        return semesterMapper.selectCurrent(); // 调用Mapper查询当前学期
    }

    @Override
    public void addSemester(Semester semester) {
        if (semester.getSemesterName() == null || semester.getSemesterName().trim().isEmpty()) { // 校验学期名称是否为空
            throw new IllegalArgumentException("学期名称不能为空"); // 名称为空则抛出异常
        }
        if (semester.getStartDate() == null) { // 校验开始日期是否为空
            throw new IllegalArgumentException("开始日期不能为空"); // 开始日期为空则抛出异常
        }
        if (semester.getEndDate() == null) { // 校验结束日期是否为空
            throw new IllegalArgumentException("结束日期不能为空"); // 结束日期为空则抛出异常
        }
        if (semester.getStartDate().isAfter(semester.getEndDate())) { // 校验开始日期是否晚于结束日期
            throw new IllegalArgumentException("开始日期不能晚于结束日期"); // 日期不合法则抛出异常
        }
        semester.setSemesterName(semester.getSemesterName().trim()); // 设置去除空格后的学期名称
        if (semester.getStatus() == null || semester.getStatus().trim().isEmpty()) { // 判断状态是否为空
            semester.setStatus("ACTIVE"); // 设置默认状态为ACTIVE（激活）
        }
        // 如果设置为当前学期，先清除其他学期的当前标识
        if (Boolean.TRUE.equals(semester.getIsCurrent())) { // 判断是否将该学期设为当前学期
            semesterMapper.clearCurrent(); // 清除所有学期的当前标识
        } else {
            semester.setIsCurrent(false); // 确保未设置当前标识时为false
        }
        semesterMapper.insert(semester); // 调用Mapper将学期信息插入数据库
    }

    @Override
    public void updateSemester(Semester semester) {
        if (semester.getSemesterId() == null) { // 校验学期ID是否为空
            throw new IllegalArgumentException("学期ID不能为空"); // ID为空则抛出异常
        }
        if (semester.getStartDate() != null && semester.getEndDate() != null
                && semester.getStartDate().isAfter(semester.getEndDate())) { // 判断开始日期和结束日期是否都非空且开始日期晚于结束日期
            throw new IllegalArgumentException("开始日期不能晚于结束日期"); // 日期不合法则抛出异常
        }
        semesterMapper.update(semester); // 调用Mapper更新学期信息
    }

    @Override
    public void deleteSemester(Long semesterId) {
        if (semesterId == null) { // 校验学期ID是否为空
            throw new IllegalArgumentException("学期ID不能为空"); // ID为空则抛出异常
        }
        Semester semester = semesterMapper.selectById(semesterId); // 根据ID查询学期信息
        if (semester != null && Boolean.TRUE.equals(semester.getIsCurrent())) { // 判断该学期是否为当前学期
            throw new IllegalArgumentException("不能删除当前学期"); // 当前学期不允许删除
        }
        semesterMapper.deleteById(semesterId); // 调用Mapper根据ID删除学期记录
    }

    @Override
    @Transactional
    public void setCurrentSemester(Long semesterId) {
        if (semesterId == null) { // 校验学期ID是否为空
            throw new IllegalArgumentException("学期ID不能为空"); // ID为空则抛出异常
        }
        Semester semester = semesterMapper.selectById(semesterId); // 根据ID查询学期信息
        if (semester == null) { // 判断学期是否存在
            throw new IllegalArgumentException("学期不存在"); // 学期不存在则抛出异常
        }
        // 先清除所有学期的当前标识，再设置指定学期为当前
        semesterMapper.clearCurrent(); // 清除所有学期的当前标识
        semesterMapper.setCurrent(semesterId); // 将指定学期设置为当前学期
    }
}
