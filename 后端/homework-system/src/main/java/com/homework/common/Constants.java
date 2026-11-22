package com.homework.common;

/**
 * 系统常量定义
 */
// 系统常量类，集中存放系统中使用到的固定常量值，便于统一维护
public class Constants {

    /** 角色 - 管理员 */
    // 管理员角色标识字符串，用于权限判断
    public static final String ROLE_ADMIN = "ADMIN";

    /** 角色 - 教师 */
    // 教师角色标识字符串，用于权限判断
    public static final String ROLE_TEACHER = "TEACHER";

    /** 角色 - 学生 */
    // 学生角色标识字符串，用于权限判断
    public static final String ROLE_STUDENT = "STUDENT";

    /** Token 请求头名称 */
    // HTTP 请求头中存放 Token 的字段名称
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /** Token 前缀 */
    // JWT Token 在请求头中的固定前缀，格式为 "Bearer "
    public static final String TOKEN_PREFIX = "Bearer ";

    /** Token 请求头中的用户ID */
    // 请求头中传递用户 ID 的自定义字段名称
    public static final String HEADER_USER_ID = "X-User-Id";

    /** Token 请求头中的角色 */
    // 请求头中传递用户角色的自定义字段名称
    public static final String HEADER_USER_ROLE = "X-User-Role";

    /** 默认密码（新增用户/重置密码时） */
    // 新增用户或重置密码时使用的默认密码
    public static final String DEFAULT_PASSWORD = "123456";
}
