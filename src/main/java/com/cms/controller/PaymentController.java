package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.cms.service.payment.PaymentService;
import com.cms.service.sse.SSEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Payment", description = "Các API xử lý giao dịch và thanh toán đơn hàng")
@RequiredArgsConstructor
@RestController
@RequestMapping("${server.api-prefix}/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final SSEService sseService;

    @Operation(summary = "Sub event thanh toán", description = "Nhận thông báo từ event")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String orderId) {
        return sseService.subPayment(orderId);
    }

    @Operation(summary = "Tạo thanh toán", description = "Tạo yêu cầu thanh toán mới cho một đơn hàng (hỗ trợ VNPay, etc).")
    @PostMapping()
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest, 
            HttpServletRequest request) {
        
        PaymentResponse response = paymentService.createPayment(paymentRequest, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @Operation(summary = "Xử lý callback thanh toán", description = "Nhận và xử lý kết quả trả về từ cổng thanh toán sau khi người dùng thực hiện giao dịch.")
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<PaymentCallbackResponse>> paymentCallback(HttpServletRequest request) {
        PaymentCallbackResponse response = paymentService.paymentCallback(request);
        sseService.sendEventPayment(
                response.getOrderId(),
                "payment-status",
                response.getStatus()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}