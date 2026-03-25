package com.cms.common.enums;

/**
 * Loại provider xác thực - dùng trong Factory Pattern
 */
public enum AuthProviderType {
    LOCAL,      // Đăng nhập bằng email/password thông thường
    GOOGLE,     // Đăng nhập qua Google OAuth2
    FACEBOOK    // Đăng nhập qua Facebook (mở rộng sau)
}
