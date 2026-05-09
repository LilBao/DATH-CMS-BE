package com.cms.util;

import com.cms.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {

    /**
     * Lấy thông tin UserPrincipal hiện tại từ SecurityContext
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        return Optional.empty();
    }

    /**
     * Lấy UserId hiện tại
     */
    public static String getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getUserId).orElse(null);
    }

    /**
     * Lấy BranchId hiện tại (nếu là nhân viên)
     */
    public static Integer getCurrentBranchId() {
        return getCurrentUser().map(UserPrincipal::getBranchId).orElse(null);
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là Admin không
     */
    public static boolean isAdmin() {
        return getCurrentUser()
                .map(u -> u.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
                .orElse(false);
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là Manager không
     */
    public static boolean isManager() {
        return getCurrentUser()
                .map(u -> u.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")))
                .orElse(false);
    }
}
