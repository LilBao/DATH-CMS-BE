package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentDetailResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.cms.service.customer.CustomerService;
import com.cms.service.payment.PaymentService;
import com.cms.service.sse.SSEService;
import com.cms.security.CurrentUser;
import com.cms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@Tag(name = "Payment", description = "Các API xử lý giao dịch và thanh toán đơn hàng")
@RequiredArgsConstructor
@RestController
@RequestMapping("${server.api-prefix}/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final SSEService sseService;

    @Operation(summary = "Sub event thanh toán", description = "Nhận thông báo từ event")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String orderId) {
        return sseService.subPayment(orderId);
    }

    @Operation(summary = "Tạo thanh toán", description = "Tạo yêu cầu thanh toán mới cho một đơn hàng.")
    @PostMapping()
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest, 
            HttpServletRequest request) {
        
        PaymentResponse response = paymentService.createPayment(paymentRequest, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @Operation(summary = "Lấy thông tin thanh toán theo mã đơn hàng")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDetailResponse>> getByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByOrderId(orderId)));
    }

    @Operation(summary = "Lấy lịch sử thanh toán của tôi")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PaymentDetailResponse>>> getMyPayments(
            @CurrentUser UserDetails userDetails) {
        String userID = customerService.getByEmail(userDetails.getUsername()).getCUserId();
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentHistory(userID)));
    }

    @Operation(summary = "Xử lý IPN thanh toán (POST/GET)", description = "Webhook nhận thông báo trạng thái từ cổng thanh toán.")
    @RequestMapping(value = "/ipn", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Void> paymentIPN(HttpServletRequest request) {
        log.info("Received IPN request");
        PaymentCallbackResponse response = paymentService.processIPN(request);
        
        // Notify via SSE
        sseService.sendEventPayment(
                response.getOrderId(),
                "payment-status",
                response.getStatus()
        );
        
        // Trả về 204 No Content hoặc 200 OK tùy yêu cầu của cổng thanh toán
        return ResponseEntity.ok().build();
    }
}