package com.cms.auth.controller;

import com.cms.auth.dto.*;
import com.cms.auth.service.AuthService;
import com.cms.common.response.ApiResponse;
import com.cms.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Auth REST Controller
 *
 * POST /auth/register    → Đăng ký tài khoản mới
 * POST /auth/login       → Đăng nhập (LOCAL hoặc GOOGLE)
 * POST /auth/refresh     → Làm mới access token
 * POST /auth/logout      → Đăng xuất
 * GET  /auth/me          → Lấy thông tin user hiện tại
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/register
     * Đăng ký tài khoản Customer mới bằng email/password
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(response));
    }


    /**
     * POST /auth/login
     *
     * Body cho LOCAL login:
     * {
     *   "email": "user@example.com",
     *   "password": "secret123",
     *   "provider": "LOCAL"
     * }
     *
     * Body cho GOOGLE login:
     * {
     *   "idToken": "google-id-token-here",
     *   "provider": "GOOGLE"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    /**
     * POST /auth/refresh
     * Body: { "refreshToken": "..." }
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        JwtResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
    }


    /**
     * POST /auth/logout
     * Requires: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        String userId = currentUser != null ? currentUser.getUserId() : null;
        authService.logout(token, userId);

        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }


    /**
     * GET /auth/me
     * Lấy thông tin user đang đăng nhập
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> me(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Not authenticated"));
        }

        UserInfoResponse info = UserInfoResponse.builder()
                .userId(currentUser.getUserId())
                .email(currentUser.getEmail())
                .role(currentUser.getAuthorities().iterator().next().getAuthority()
                        .replace("ROLE_", ""))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(info));
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class UserInfoResponse {
        private String userId;
        private String email;
        private String role;
    }
}
