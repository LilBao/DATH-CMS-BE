package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.ChangePasswordRequest;
import com.cms.security.UserPrincipal;
import com.cms.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${server.api-prefix}/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Các API quản lý tài khoản người dùng chung")
public class UserController {

    private final UserService userService;

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Thay đổi mật khẩu cho người dùng hiện tại (áp dụng cho cả Customer và Employee)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }
}
