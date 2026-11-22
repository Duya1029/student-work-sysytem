package com.homework.dto;

/**
 * 重置密码请求 DTO
 */
public class ResetPwdDTO {
    // 用户ID
    private Long userId;
    // 新密码
    private String newPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
