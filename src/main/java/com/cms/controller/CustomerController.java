package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.response.CustomerResponse;
import com.cms.service.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Các API quản lý khách hàng")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khách hàng")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin khách hàng theo ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getById(id)));
    }

    @GetMapping("/email")
    @Operation(summary = "Lấy thông tin khách hàng theo email")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByEmail(email)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Ngừng kích hoạt tài khoản khách hàng")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        customerService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deactivated", null));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt lại tài khoản khách hàng")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable String id) {
        customerService.activate(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer activated", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông tin khách hàng")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted successfully", null));
    }
}
