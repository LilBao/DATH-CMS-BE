package com.cms.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Quản lý blacklist JWT tokens trong Redis.
 * Dùng khi logout - lưu token vào Redis với TTL = thời gian còn lại của token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenBlacklist {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Thêm token vào blacklist
     * @param token JWT token cần vô hiệu hoá
     */
    public void blacklistToken(String token) {
        try {
            long remainingMs = jwtTokenProvider.getRemainingTimeMs(token);
            if (remainingMs > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "blacklisted",
                        remainingMs, TimeUnit.MILLISECONDS);
                log.debug("Token blacklisted, expires in {}ms", remainingMs);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    /**
     * Kiểm tra token có trong blacklist không
     * @return true nếu đã bị vô hiệu hoá
     */
    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(
                    redisTemplate.hasKey(BLACKLIST_PREFIX + token)
            );
        } catch (Exception e) {
            log.error("Failed to check blacklist: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Lưu refresh token vào Redis để quản lý
     */
    public void storeRefreshToken(String userId, String refreshToken, long ttlMs) {
        String key = "jwt:refresh:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, ttlMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Lấy refresh token đang lưu của user
     */
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("jwt:refresh:" + userId);
    }

    /**
     * Xoá refresh token khi logout
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("jwt:refresh:" + userId);
    }
}
