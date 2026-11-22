package com.homework.mapper;

import com.homework.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口 - 对应 UserMapper.xml
 */
public interface UserMapper {

    // 根据用户名查询用户（登录用）
    User selectByUsername(@Param("username") String username);

    // 根据用户ID查询用户信息（含班级名称）
    User selectById(@Param("userId") Long userId);

    // 分页查询用户列表（含班级名称）
    // @param offset 起始行（SQL Server 用 OFFSET ... FETCH NEXT 分页）
    // @param pageSize 每页条数
    // @param role 角色（可选）
    // @param realName 真实姓名（模糊查询）
    // @param classId 班级ID（可选）
    List<User> selectList(@Param("offset") int offset,
                          @Param("pageSize") int pageSize,
                          @Param("role") String role,
                          @Param("realName") String realName,
                          @Param("classId") Long classId);

    // 查询用户总数（用于分页计算）
    int selectCount(@Param("role") String role,
                    @Param("realName") String realName,
                    @Param("classId") Long classId);

    // 新增用户
    int insert(User user);

    // 修改用户
    int update(User user);

    // 修改密码
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    // 删除用户
    int deleteById(@Param("userId") Long userId);

    // 查询教师列表（用于发布作业时选择教师）
    List<User> selectTeachers();
}
