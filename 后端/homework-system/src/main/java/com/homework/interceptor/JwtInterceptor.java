package com.homework.interceptor;

import com.homework.common.Constants;
import com.homework.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器 - 校验 Token
 * 校验通过后，将 userId 和 role 存入 request header，供后续 Controller 使用
 */
// 标记为 Spring 组件，由 Spring 容器管理其生命周期
@Component
// 实现 HandlerInterceptor 接口，在请求到达 Controller 前进行拦截处理
public class JwtInterceptor implements HandlerInterceptor {

    // 自动注入 JWT 工具类实例，用于校验和解析 Token
    @Autowired
    private JwtUtil jwtUtil;

    // 重写 preHandle 方法，在请求处理前执行，返回 true 放行，false 拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        // 判断请求方法是否为 OPTIONS（CORS 预检请求），若是则直接放行不校验 Token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 Authorization 请求头
        // 从请求头中获取 Authorization 字段的值（即携带 Token 的完整字符串）
        String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);

        // 判断请求头是否为空或是否不以 "Bearer " 前缀开头
        if (authHeader == null || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            // 设置 HTTP 响应状态码为 401（未授权）
            response.setStatus(401);
            // 设置响应内容类型为 JSON，字符编码为 UTF-8
            response.setContentType("application/json;charset=UTF-8");
            // 向响应输出流写入未登录提示的 JSON 字符串
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token缺失\",\"data\":null}");
            // 返回 false 拦截请求，不再继续执行 Controller
            return false;
        }

        // 提取 Token
        // 去掉 "Bearer " 前缀，截取真正的 JWT Token 字符串
        String token = authHeader.substring(Constants.TOKEN_PREFIX.length());

        // 验证 Token
        // 调用 jwtUtil 校验 Token 是否合法且未过期，校验失败则进入分支
        if (!jwtUtil.validateToken(token)) {
            // 设置 HTTP 响应状态码为 401（未授权）
            response.setStatus(401);
            // 设置响应内容类型为 JSON，字符编码为 UTF-8
            response.setContentType("application/json;charset=UTF-8");
            // 向响应输出流写入 Token 无效提示的 JSON 字符串
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\",\"data\":null}");
            // 返回 false 拦截请求，不再继续执行 Controller
            return false;
        }

        // 解析 Token，获取用户信息
        // 解析 Token 获取其中的声明信息（Claims 对象）
        Claims claims = jwtUtil.parseToken(token);
        // 从 Claims 中获取 userId（Long 类型）
        Long userId = claims.get("userId", Long.class);
        // 从 Claims 中获取 username（String 类型）
        String username = claims.get("username", String.class);
        // 从 Claims 中获取 role（String 类型）
        String role = claims.get("role", String.class);

        // 将用户信息存入 request attribute，供 Controller 使用
        // 将 userId 存入 request 的属性中，便于后续 Controller 获取
        request.setAttribute("userId", userId);
        // 将 username 存入 request 的属性中
        request.setAttribute("username", username);
        // 将 role 存入 request 的属性中
        request.setAttribute("role", role);

        // 校验通过，返回 true 放行请求
        return true;
    }
}
