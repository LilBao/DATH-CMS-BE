package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.OrderRequest;
import com.cms.dto.response.OrderResponse;
import com.cms.security.CurrentUser;
import com.cms.service.booking.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Các API quản lý đơn hàng")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả đơn hàng")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin đơn hàng theo ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getById(id)));
    }

    @GetMapping("/email")
    @Operation(summary = "Lấy thông tin đơn hàng theo email")
    public ResponseEntity<ApiResponse<OrderResponse>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getByEmail(email)));
    }

    @PostMapping
    @Operation(summary ="Tạo đơn hàng")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@CurrentUser UserDetails userDetails, @RequestBody OrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(orderService.createOrder(userDetails, request)));
    }
}