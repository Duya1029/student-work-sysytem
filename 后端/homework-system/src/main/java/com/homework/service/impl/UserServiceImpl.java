package com.homework.service.impl;

import com.homework.dto.LoginDTO;
import com.homework.dto.LoginVO;
import com.homework.dto.RegisterDTO;
import com.homework.entity.User;
import com.homework.mapper.UserMapper;
import com.homework.service.UserService;
import com.homework.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper; // 用户数据访问对象，用于操作数据库中的用户表

    @Autowired
    private JwtUtil jwtUtil; // JWT工具类，用于生成和解析登录令牌

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername()); // 调用Mapper根据用户名从数据库查询用户记录
        if (user == null) { // 判断查询结果是否为空
            throw new IllegalArgumentException("用户名不存在"); // 用户不存在则抛出异常
        }

        // 2. 校验密码（明文比较，实际项目应使用 BCrypt 加密）
        if (!user.getPassword().equals(loginDTO.getPassword())) { // 将数据库中存储的密码与前端传入的密码进行比较
            throw new IllegalArgumentException("密码错误"); // 密码不匹配则抛出异常
        }

        // 3. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole()); // 调用JWT工具生成包含用户ID、用户名和角色的令牌

        // 4. 组装返回数据
        LoginVO vo = new LoginVO(); // 创建登录返回视图对象
        vo.setUserId(user.getUserId()); // 设置用户ID到返回对象
        vo.setUsername(user.getUsername()); // 设置用户名到返回对象
        vo.setRealName(user.getRealName()); // 设置真实姓名到返回对象
        vo.setRole(user.getRole()); // 设置角色到返回对象
        vo.setToken(token); // 设置JWT令牌到返回对象

        // 5. 如果是学生，查询班级名称
        if (user.getClassId() != null) { // 判断用户是否有关联的班级ID（通常是学生才有）
            User fullUser = userMapper.selectById(user.getUserId()); // 根据用户ID再次查询完整用户信息（包含班级名称）
            if (fullUser != null) { // 判断完整用户信息是否查询成功
                vo.setClassId(fullUser.getClassId()); // 设置班级ID到返回对象
                vo.setClassName(fullUser.getClassName()); // 设置班级名称到返回对象
            }
        }

        return vo; // 返回组装好的登录视图对象
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId); // 根据用户ID调用Mapper查询用户并返回
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.trim().isEmpty()) { // 校验原密码是否为空
            throw new IllegalArgumentException("原密码不能为空"); // 原密码为空则抛出异常
        }
        if (newPassword == null || newPassword.trim().isEmpty()) { // 校验新密码是否为空
            throw new IllegalArgumentException("新密码不能为空"); // 新密码为空则抛出异常
        }
        User user = userMapper.selectByUsername(username); // 根据用户名查询用户记录
        if (user == null) { // 判断用户是否存在
            throw new IllegalArgumentException("用户不存在"); // 用户不存在则抛出异常
        }
        // 校验原密码
        if (!user.getPassword().equals(oldPassword)) { // 将数据库中的密码与传入的原密码进行比较
            throw new IllegalArgumentException("原密码错误"); // 原密码不匹配则抛出异常
        }
        userMapper.updatePassword(user.getUserId(), newPassword.trim()); // 调用Mapper更新用户密码
    }

    @Override
    public LoginVO register(RegisterDTO registerDTO) {
        // 1. 校验账号：非空且至少 3 个字符
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        String username = registerDTO.getUsername().trim();
        if (username.length() < 3) {
            throw new IllegalArgumentException("账号至少需要 3 个字符");
        }

        // 2. 校验密码：至少 6 位
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("密码至少需要 6 位");
        }

        // 3. 校验真实姓名
        if (registerDTO.getRealName() == null || registerDTO.getRealName().trim().isEmpty()) {
            throw new IllegalArgumentException("真实姓名不能为空");
        }

        // 4. 校验角色：只允许注册教师或学生，防止注册管理员
        String role = registerDTO.getRole();
        if (!"TEACHER".equals(role) && !"STUDENT".equals(role)) {
            throw new IllegalArgumentException("只能注册教师或学生账号");
        }

        // 5. 学生必须选择班级
        if ("STUDENT".equals(role) && registerDTO.getClassId() == null) {
            throw new IllegalArgumentException("学生注册必须选择班级");
        }

        // 6. 校验账号是否已被占用
        if (userMapper.selectByUsername(username) != null) {
            throw new IllegalArgumentException("该账号已被注册，请更换账号");
        }

        // 7. 组装用户实体并写入数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(registerDTO.getPassword());
        user.setRealName(registerDTO.getRealName().trim());
        user.setRole(role);
        // 只有学生才保存班级和学号
        user.setClassId("STUDENT".equals(role) ? registerDTO.getClassId() : null);
        user.setStudentNo("STUDENT".equals(role) ? registerDTO.getStudentNo() : null);
        userMapper.insert(user);

        // 8. 注册成功后自动登录，返回 Token 和用户信息
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(registerDTO.getPassword());
        return login(loginDTO);
    }
}
