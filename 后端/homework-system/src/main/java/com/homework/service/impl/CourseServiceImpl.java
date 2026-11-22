package com.homework.service.impl;

import com.homework.entity.Course;
import com.homework.mapper.CourseMapper;
import com.homework.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper; // 课程数据访问对象，用于操作课程表

    @Override
    public List<Course> getCourseList(int pageNum, int pageSize, String courseName, Long teacherId, Long semesterId) {
        int offset = (pageNum - 1) * pageSize; // 计算分页查询的起始偏移量
        return courseMapper.selectList(offset, pageSize, courseName, teacherId, semesterId); // 调用Mapper分页查询课程列表
    }

    @Override
    public int getCourseCount(String courseName, Long teacherId, Long semesterId) {
        return courseMapper.selectCount(courseName, teacherId, semesterId); // 调用Mapper查询符合条件的课程总数
    }

    @Override
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) { // 校验教师ID是否为空
            throw new IllegalArgumentException("教师ID不能为空"); // ID为空则抛出异常
        }
        return courseMapper.selectByTeacherId(teacherId); // 调用Mapper查询指定教师的所有课程
    }

    @Override
    public List<Course> getCoursesByClassId(Long classId) {
        if (classId == null) { // 校验班级ID是否为空
            throw new IllegalArgumentException("班级ID不能为空"); // ID为空则抛出异常
        }
        return courseMapper.selectByClassId(classId); // 调用Mapper查询指定班级的所有课程
    }

    @Override
    public Course getCourseById(Long courseId) {
        if (courseId == null) { // 校验课程ID是否为空
            throw new IllegalArgumentException("课程ID不能为空"); // ID为空则抛出异常
        }
        return courseMapper.selectById(courseId); // 调用Mapper根据ID查询课程详情
    }

    @Override
    @Transactional
    public void addCourse(Course course) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) { // 校验课程名称是否为空
            throw new IllegalArgumentException("课程名称不能为空"); // 名称为空则抛出异常
        }
        if (course.getTeacherId() == null) { // 校验任课教师ID是否为空
            throw new IllegalArgumentException("请选择任课教师"); // 教师ID为空则抛出异常
        }
        course.setCourseName(course.getCourseName().trim()); // 设置去除空格后的课程名称
        if (course.getCourseCode() != null) { // 判断课程编号是否非空
            course.setCourseCode(course.getCourseCode().trim()); // 设置去除空格后的课程编号
        }
        courseMapper.insert(course); // 调用Mapper将课程信息插入数据库
        // 关联班级（classIds 为逗号分隔的字符串）
        saveCourseClasses(course.getCourseId(), course.getClassIds()); // 保存课程与班级的关联关系
    }

    @Override
    @Transactional
    public void updateCourse(Course course) {
        if (course.getCourseId() == null) { // 校验课程ID是否为空
            throw new IllegalArgumentException("课程ID不能为空"); // ID为空则抛出异常
        }
        if (course.getCourseName() != null && course.getCourseName().trim().isEmpty()) { // 判断课程名称是否为空字符串
            throw new IllegalArgumentException("课程名称不能为空"); // 名称为空字符串则抛出异常
        }
        if (course.getCourseName() != null) { // 判断课程名称是否非空
            course.setCourseName(course.getCourseName().trim()); // 设置去除空格后的课程名称
        }
        if (course.getCourseCode() != null) { // 判断课程编号是否非空
            course.setCourseCode(course.getCourseCode().trim()); // 设置去除空格后的课程编号
        }
        courseMapper.update(course); // 调用Mapper更新课程信息
        // 如果传入了新的 classIds，则更新关联
        if (course.getClassIds() != null) { // 判断是否传入了新的班级关联
            courseMapper.deleteCourseClasses(course.getCourseId()); // 删除该课程原有的班级关联
            saveCourseClasses(course.getCourseId(), course.getClassIds()); // 重新保存课程与班级的关联关系
        }
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        if (courseId == null) { // 校验课程ID是否为空
            throw new IllegalArgumentException("课程ID不能为空"); // ID为空则抛出异常
        }
        // 先清理关联班级，再删除课程
        courseMapper.deleteCourseClasses(courseId); // 删除该课程与班级的关联记录
        courseMapper.deleteById(courseId); // 调用Mapper根据ID删除课程记录
    }

    /**
     * 保存课程-班级关联关系
     * @param courseId 课程ID
     * @param classIdsStr 逗号分隔的班级ID字符串
     */
    private void saveCourseClasses(Long courseId, String classIdsStr) {
        if (classIdsStr == null || classIdsStr.trim().isEmpty()) { // 校验班级ID字符串是否为空
            return; // 为空则直接返回，不做处理
        }
        List<String> classIdList = Arrays.asList(classIdsStr.split(",")); // 将逗号分隔的字符串拆分为班级ID列表
        for (String classIdStr : classIdList) { // 遍历每个班级ID字符串
            String trimmed = classIdStr.trim(); // 去除班级ID字符串前后空格
            if (trimmed.isEmpty()) { // 判断去除空格后是否为空
                continue; // 为空则跳过当前循环
            }
            try {
                Long classId = Long.parseLong(trimmed); // 将班级ID字符串转换为Long类型
                courseMapper.insertCourseClass(courseId, classId); // 插入课程与班级的关联记录
            } catch (NumberFormatException e) {
                // 跳过无法解析的班级ID
            }
        }
    }
}
