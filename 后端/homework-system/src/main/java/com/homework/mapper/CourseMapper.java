package com.homework.mapper;

import com.homework.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper 接口 - 对应 CourseMapper.xml
 */
public interface CourseMapper {

    // 分页查询课程列表（含教师名、学期名、班级名）
    List<Course> selectList(@Param("offset") int offset,
                            @Param("pageSize") int pageSize,
                            @Param("courseName") String courseName,
                            @Param("teacherId") Long teacherId,
                            @Param("semesterId") Long semesterId);

    // 查询课程总数
    int selectCount(@Param("courseName") String courseName,
                    @Param("teacherId") Long teacherId,
                    @Param("semesterId") Long semesterId);

    // 根据教师ID查询课程列表
    List<Course> selectByTeacherId(@Param("teacherId") Long teacherId);

    // 根据班级ID查询课程列表（通过 sys_course_class 关联）
    List<Course> selectByClassId(@Param("classId") Long classId);

    // 根据ID查询课程
    Course selectById(@Param("courseId") Long courseId);

    // 新增课程
    int insert(Course course);

    // 修改课程
    int update(Course course);

    // 删除课程（同时会通过数据库外键级联或手动清理关联班级）
    int deleteById(@Param("courseId") Long courseId);

    // 删除课程关联的所有班级记录
    int deleteCourseClasses(@Param("courseId") Long courseId);

    // 新增课程-班级关联
    int insertCourseClass(@Param("courseId") Long courseId, @Param("classId") Long classId);
}
