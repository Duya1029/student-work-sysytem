package com.homework.mapper;

import com.homework.entity.Homework;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业 Mapper 接口 - 对应 HomeworkMapper.xml
 */
public interface HomeworkMapper {

    // 分页查询作业列表（管理员视角，包含教师名和班级名）
    List<Homework> selectListForAdmin(@Param("offset") int offset,
                                      @Param("pageSize") int pageSize,
                                      @Param("className") String className,
                                      @Param("teacherName") String teacherName);

    // 查询作业总数（管理员视角）
    int selectCountForAdmin(@Param("className") String className,
                            @Param("teacherName") String teacherName);

    // 分页查询教师的作业列表
    List<Homework> selectListByTeacher(@Param("teacherId") Long teacherId,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    // 查询教师的作业总数
    int selectCountByTeacher(@Param("teacherId") Long teacherId);

    // 分页查询学生的作业列表（通过班级关联，含提交状态）
    List<Homework> selectListForStudent(@Param("classId") Long classId,
                                        @Param("studentId") Long studentId,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);

    // 查询学生的作业总数
    int selectCountForStudent(@Param("classId") Long classId);

    // 根据ID查询作业详情
    Homework selectById(@Param("homeworkId") Long homeworkId);

    // 新增作业
    int insert(Homework homework);

    // 修改作业
    int update(Homework homework);

    // 删除作业
    int deleteById(@Param("homeworkId") Long homeworkId);
}
