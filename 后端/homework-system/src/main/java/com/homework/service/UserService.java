package com.homework.service;

import com.homework.dto.LoginDTO;
import com.homework.dto.LoginVO;
import com.homework.dto.RegisterDTO;
import com.homework.entity.User;

/**
 * 用户服务接口（登录、用户信息）
 */
public interface UserService {

    /**
     * 登录
     */
    // 用户登录验证，根据账号密码校验身份并返回登录结果视图对象
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户ID获取用户信息
     */
    // 根据用户唯一标识查询用户详细信息
    User getUserById(Long userId);

    /**
     * 修改密码（需验证原密码）
     */
    // 修改用户密码，需先校验原密码是否正确，校验通过后更新为新密码
    void changePassword(String username, String oldPassword, String newPassword);

    /**
     * 自助注册（学生/教师）
     * 注册成功后直接返回登录信息，实现注册即登录
     */
    // 校验注册信息并创建用户账号，注册成功后自动生成 Token 返回
    LoginVO register(RegisterDTO registerDTO);
}
