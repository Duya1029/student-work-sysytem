package com.homework.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 - 用于生成和解析 Token
 */
// 标记为 Spring 组件，可被自动注入使用
@Component
public class JwtUtil {

    // 从配置文件中读取 JWT 签名密钥（对应配置项 jwt.secret）
    @Value("${jwt.secret}")
    private String secret;

    // 从配置文件中读取 Token 过期时间（毫秒，对应配置项 jwt.expiration）
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 获取签名密钥
     */
    // 私有方法：根据配置的 secret 字符串生成 HMAC-SHA 签名密钥
    private SecretKey getSigningKey() {
        // 使用 secret 的 UTF-8 字节数组生成符合 HS256 等算法要求的密钥
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     * @return JWT Token
     */
    // 根据用户信息生成 JWT Token 字符串
    public String generateToken(Long userId, String username, String role) {
        // 创建用于存放自定义声明的 Map
        Map<String, Object> claims = new HashMap<>();
        // 将用户 ID 放入声明中
        claims.put("userId", userId);
        // 将用户名放入声明中
        claims.put("username", username);
        // 将用户角色放入声明中
        claims.put("role", role);

        // 获取当前时间，作为 Token 的签发时间
        Date now = new Date();
        // 计算过期时间 = 当前时间 + 配置的过期时长（毫秒）
        Date expiryDate = new Date(now.getTime() + expiration);

        // 使用 Jwts 构建器生成并返回紧凑格式的 JWT 字符串
        return Jwts.builder()
                // 设置自定义声明（payload 中的自定义字段）
                .claims(claims)
                // 设置主题（subject）为用户名
                .subject(username)
                // 设置签发时间
                .issuedAt(now)
                // 设置过期时间
                .expiration(expiryDate)
                // 使用签名密钥对 Token 进行签名
                .signWith(getSigningKey())
                // 生成紧凑的 JWT 字符串
                .compact();
    }

    /**
     * 解析 Token
     */
    // 解析 JWT Token 字符串，返回其中的声明信息（Claims）
    public Claims parseToken(String token) {
        // 创建 JWT 解析器并验证签名，解析已签名的 Claims 后获取 payload 部分
        return Jwts.parser()
                // 设置签名密钥用于校验 Token 签名
                .verifyWith(getSigningKey())
                // 构建解析器
                .build()
                // 解析 Token 并获取已签名的 Claims
                .parseSignedClaims(token)
                // 获取 Claims 的 payload 部分
                .getPayload();
    }

    /**
     * 从 Token 中获取用户ID
     */
    // 从给定 Token 中提取用户 ID
    public Long getUserIdFromToken(String token) {
        // 先解析 Token 获取 Claims
        Claims claims = parseToken(token);
        // 从 Claims 中获取 userId（Long 类型）并返回
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    // 从给定 Token 中提取用户名
    public String getUsernameFromToken(String token) {
        // 先解析 Token 获取 Claims
        Claims claims = parseToken(token);
        // 从 Claims 中获取 username（String 类型）并返回
        return claims.get("username", String.class);
    }

    /**
     * 从 Token 中获取角色
     */
    // 从给定 Token 中提取用户角色
    public String getRoleFromToken(String token) {
        // 先解析 Token 获取 Claims
        Claims claims = parseToken(token);
        // 从 Claims 中获取 role（String 类型）并返回
        return claims.get("role", String.class);
    }

    /**
     * 验证 Token 是否有效
     */
    // 校验 Token 是否有效（签名正确且未过期）
    public boolean validateToken(String token) {
        try {
            // 尝试解析 Token，解析成功说明签名正确且未过期
            parseToken(token);
            // 解析无异常，返回 true 表示 Token 有效
            return true;
        } catch (Exception e) {
            // 解析抛出异常（签名错误、过期、格式错误等），返回 false 表示 Token 无效
            return false;
        }
    }
}
