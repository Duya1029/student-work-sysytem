package com.homework.config;

import com.homework.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 1. 注册 JWT 拦截器，对需要认证的接口进行 Token 校验
 * 2. 配置跨域（CORS）
 */
// 标记为 Spring 配置类，用于自定义 Web MVC 相关配置
@Configuration
// 实现 WebMvcConfigurer 接口，重写拦截器注册和跨域配置方法
public class WebConfig implements WebMvcConfigurer {

    // 自动注入 JWT 拦截器实例
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截器
     */
    // 重写 addInterceptors 方法，向 Spring MVC 注册自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将 jwtInterceptor 注册到拦截器链中
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有以 /api/ 开头的请求路径
                .addPathPatterns("/api/**")          // 拦截所有 /api/** 请求
                // 排除不需要 Token 校验的路径：登录、自助注册、注册页班级列表
                .excludePathPatterns(               // 排除登录接口
                        "/api/login",
                        "/api/register",
                        "/api/register/classes"
                );
    }

    /**
     * 跨域配置
     */
    // 重写 addCorsMappings 方法，配置跨域资源共享规则
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 为所有以 /api/ 开头的请求路径添加跨域映射
        registry.addMapping("/api/**")
                // 允许所有来源的请求（使用通配符匹配）
                .allowedOriginPatterns("*")
                // 允许的 HTTP 请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 允许携带凭证（如 Cookie、Authorization 头）
                .allowCredentials(true)
                // 预检请求的缓存时间，单位为秒（3600秒=1小时）
                .maxAge(3600);
    }
}
