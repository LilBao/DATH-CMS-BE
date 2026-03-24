package com.cgv.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * JWT Response trả về sau khi đăng nhập thành công
 */
@Data
@Builder
public class JwtResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresIn;       // seconds

    // User info
    private String userId;
    private String email;
    private String fullName;
    private String role;
    private String avatarUrl;
}
