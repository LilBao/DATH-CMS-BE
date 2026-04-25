package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.CouponRequest;
import com.cms.dto.response.CouponResponse;
import com.cms.service.booking.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Các API quản lý mã giảm giá")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả coupon")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(couponService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin coupon theo ID")
    public ResponseEntity<ApiResponse<CouponResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(couponService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(couponService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", couponService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một coupon")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Coupon deleted successfully", null));
    }
}
