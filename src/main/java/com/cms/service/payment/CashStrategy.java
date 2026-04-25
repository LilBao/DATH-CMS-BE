package com.cms.service.payment;

import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CashStrategy implements PaymentStrategy {

    @Override
    public boolean supports(String paymentMethod) {
        return "CASH".equalsIgnoreCase(paymentMethod);
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        return PaymentResponse.builder()
                .message("Cash payment processing initiated")
                .status("SUCCESS")
                .build();
    }

    @Override
    public PaymentCallbackResponse processIPN(HttpServletRequest httpRequest) {
        return PaymentCallbackResponse.builder()
                .status("SUCCESS")
                .message("Cash payment verified")
                .build();
    }
}
