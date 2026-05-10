package com.cms.service.payment;

import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentStrategy {
    boolean supports(String paymentMethod);
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest);
    PaymentCallbackResponse processIPN(HttpServletRequest httpRequest);
}
