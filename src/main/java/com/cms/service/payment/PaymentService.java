package com.cms.service.payment;

import com.cms.dto.payment.PaymentDetailResponse;
import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest);
    PaymentCallbackResponse processIPN(HttpServletRequest httpRequest);
    PaymentDetailResponse getPaymentByOrderId(Integer orderId);
    List<PaymentDetailResponse> getPaymentHistory(String userId);
}
