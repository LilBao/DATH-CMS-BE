package com.cms.auth.strategy;

import com.cms.auth.dto.LoginRequest;
import com.cms.auth.dto.JwtResponse;
import com.cms.common.enums.AuthProviderType;

/**
 * ============================================================
 * DESIGN PATTERN: STRATEGY
 * ============================================================
 * Interface AuthStrategy định nghĩa contract cho tất cả
 * các strategy xác thực (Local, Google, Facebook...).
 *
 * Các implementation:
 *   - LocalAuthStrategy  → email/password thông thường
 *   - GoogleAuthStrategy → OAuth2 Google
 *
 * AuthStrategyContext sẽ quyết định triển khai nào được dùng
 * dựa vào AuthProviderType.
 * ============================================================
 */
public interface AuthStrategy {

    /**
     * Loại provider này hỗ trợ
     */
    AuthProviderType getType();

    /**
     * Xử lý đăng nhập và trả về JWT response
     * @param request DTO chứa thông tin xác thực
     * @return JwtResponse với access token và refresh token
     */
    JwtResponse authenticate(LoginRequest request);

    /**
     * Kiểm tra provider này có hỗ trợ loại request không
     */
    default boolean supports(AuthProviderType type) {
        return getType() == type;
    }
}
