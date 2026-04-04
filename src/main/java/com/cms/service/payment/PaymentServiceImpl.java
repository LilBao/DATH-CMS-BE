package com.cms.service.payment;

import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.cms.service.sse.SSEService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final List<PaymentStrategy> paymentStrategies;
    private final SSEService sseService;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        String method = request.getPaymentMethod();
        PaymentStrategy strategy = getStrategy(method);
        return strategy.createPayment(request, httpRequest);
    }

    @Override
    public PaymentCallbackResponse paymentCallback(HttpServletRequest httpRequest) {
        String provider = httpRequest.getParameter("provider");
        if (provider == null || provider.isEmpty()) {
            provider = "VNPAY";
        }
        
        PaymentStrategy strategy = getStrategy(provider);
        return strategy.processCallback(httpRequest);
    }

    private PaymentStrategy getStrategy(String method) {
        return paymentStrategies.stream()
                .filter(s -> s.supports(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment method: " + method));
    }
}
