package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.UpdateProfileRequest;
import com.cms.dto.response.CustomerResponse;
import com.cms.security.UserPrincipal;
import com.cms.service.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Các API quản lý khách hàng")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách tất cả khách hàng")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAll()));
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin cá nhân của người dùng hiện tại")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByEmail(userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or #id == principal.userId")
    @Operation(summary = "Lấy thông tin khách hàng theo ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getById(id)));
    }

    @PutMapping("/me")
    @Operation(summary = "Cập nhật thông tin cá nhân")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.updateProfile(customerService.getByEmail(userDetails.getUsername()).getCUserId(), request)));
    }

    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Lấy thông tin khách hàng theo email")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByEmail(email)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Ngừng kích hoạt tài khoản khách hàng")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        customerService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deactivated", null));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Kích hoạt lại tài khoản khách hàng")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable String id) {
        customerService.activate(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer activated", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Xóa thông tin khách hàng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted successfully", null));
    }
}
