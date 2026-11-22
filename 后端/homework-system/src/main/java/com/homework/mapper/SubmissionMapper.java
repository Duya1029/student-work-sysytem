package com.homework.mapper;

import com.homework.entity.Submission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交 Mapper 接口 - 对应 SubmissionMapper.xml
 */
public interface SubmissionMapper {

    // 查询某作业的所有提交记录（教师/管理员视角）
    List<Submission> selectByHomeworkId(@Param("homeworkId") Long homeworkId);

    // 查询某学生的提交记录（通过作业ID和学生ID）
    Submission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId,
                                          @Param("studentId") Long studentId);

    // 根据提交ID查询提交记录
    Submission selectById(@Param("submissionId") Long submissionId);

    // 查询某学生的历史成绩（已批改的提交记录）
    List<Submission> selectByStudentId(@Param("studentId") Long studentId);

    // 查询某学生的提交总数（含未批改）
    int selectCountByStudent(@Param("studentId") Long studentId);

    // 查询某学生的已批改提交数
    int selectGradedCountByStudent(@Param("studentId") Long studentId);

    // 新增提交
    int insert(Submission submission);

    // 更新提交内容（重新提交）
    int updateContent(Submission submission);

    // 批改作业（更新分数、评语、批改时间）
    int updateGrade(@Param("submissionId") Long submissionId,
                    @Param("score") Integer score,
                    @Param("comment") String comment);

    // 批改作业（扩展版：包含是否打回、是否优秀）
    int updateGradeExt(@Param("submissionId") Long submissionId,
                       @Param("score") Integer score,
                       @Param("comment") String comment,
                       @Param("isReturned") Boolean isReturned,
                       @Param("isExcellent") Boolean isExcellent);

    // 删除提交记录
    int deleteById(@Param("submissionId") Long submissionId);
}
