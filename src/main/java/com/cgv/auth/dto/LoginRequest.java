package com.cgv.auth.dto;

import com.cgv.common.enums.AuthProviderType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cho đăng nhập.
 * - Local: email + password
 * - Google: idToken (từ Google OAuth2)
 */
@Data
public class LoginRequest {

    /**
     * Email người dùng (dùng cho LOCAL auth)
     */
    private String email;

    /**
     * Mật khẩu (dùng cho LOCAL auth)
     */
    private String password;

    /**
     * Google ID Token (dùng cho GOOGLE auth)
     */
    private String idToken;

    /**
     * Loại provider - bắt buộc
     */
    @NotBlank(message = "Provider type is required")
    private String provider;

    /**
     * Lấy AuthProviderType từ string
     */
    public AuthProviderType getProviderType() {
        try {
            return AuthProviderType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AuthProviderType.LOCAL;
        }
    }
}
