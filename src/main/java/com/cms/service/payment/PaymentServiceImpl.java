package com.cms.service.payment;

import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.cms.entity.booking.Order;
import com.cms.entity.booking.Payment;
import com.cms.entity.booking.PaymentHistory;
import com.cms.entity.customer.Membership;
import com.cms.enums.EOrderStatus;
import com.cms.enums.EPaymentMethod;
import com.cms.enums.EPaymentStatus;
import com.cms.repository.booking.OrderRepository;
import com.cms.repository.booking.PaymentHistoryRepository;
import com.cms.repository.booking.PaymentRepository;
import com.cms.service.sse.SSEService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final List<PaymentStrategy> paymentStrategies;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        String method = request.getPaymentMethod();

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderId()));

        Optional<Payment> existingPayment = paymentRepository.findByOrder_OrderId(request.getOrderId());
        if (existingPayment.isEmpty()) {
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(request.getAmount())
                    .paymentMethod(EPaymentMethod.valueOf(method.toUpperCase()))
                    .paymentStatus(EPaymentStatus.PENDING)
                    .paymentTime(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment);
        }

        PaymentStrategy strategy = getStrategy(method);
        return strategy.createPayment(request, httpRequest);
    }

    @Override
    @Transactional
    public PaymentCallbackResponse paymentCallback(HttpServletRequest httpRequest) {
        String provider = httpRequest.getParameter("provider");
        if (provider == null || provider.isEmpty()) {
            if ("MOMO".equals(httpRequest.getParameter("partnerCode"))) {
                provider = "MOMO";
            } else {
                provider = "VNPAY";
            }
        }
        
        PaymentStrategy strategy = getStrategy(provider);
        PaymentCallbackResponse response = strategy.processCallback(httpRequest);
        
        String orderIdStr = response.getOrderId();
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            // fallback, try to extract from VNPay order info if mapped like that
            String vnpOrderInfo = httpRequest.getParameter("vnp_OrderInfo");
            if (vnpOrderInfo != null && vnpOrderInfo.startsWith("Thanh toan don hang ")) {
                orderIdStr = vnpOrderInfo.substring("Thanh toan don hang ".length());
                response.setOrderId(orderIdStr);
            }
        }
        
        if (orderIdStr != null && !orderIdStr.isEmpty()) {
            try {
                Integer orderId = Integer.parseInt(orderIdStr);
                Optional<Payment> optionalPayment = paymentRepository.findByOrder_OrderId(orderId);
                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    
                    EPaymentStatus newStatus = "SUCCESS".equalsIgnoreCase(response.getStatus()) 
                            ? EPaymentStatus.COMPLETED 
                            : EPaymentStatus.FAILED;
                            
                    payment.setPaymentStatus(newStatus);
                    if (response.getTransactionId() != null) {
                        payment.setTransactionId(response.getTransactionId());
                    }
                    paymentRepository.save(payment);

                    if (newStatus == EPaymentStatus.COMPLETED) {
                        Order pOrder = payment.getOrder();
                        if (pOrder != null) {
                            pOrder.setOrderStatus(EOrderStatus.PAID);
                            if (pOrder.getCustomer() != null && pOrder.getCustomer().getMembership() != null) {
                                Membership membership = pOrder.getCustomer().getMembership();
                                int earnedPoints = payment.getAmount().divide(Math.BigDecimal(1000)).intValue();
                                membership.setPoint(membership.getPoint() + earnedPoints);
                            }
                            orderRepository.save(pOrder);
                        }
                    }

                    PaymentHistory history = PaymentHistory.builder()
                            .payment(payment)
                            .paymentStatus(newStatus)
                            .amount(payment.getAmount())
                            .transactionId(response.getTransactionId())
                            .responseCode(httpRequest.getParameter("resultCode") != null ? httpRequest.getParameter("resultCode") : httpRequest.getParameter("vnp_ResponseCode"))
                            .responseMessage(response.getMessage())
                            .rawResponse(httpRequest.getQueryString())
                            .build();
                    paymentHistoryRepository.save(history);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return response;
    }

    private PaymentStrategy getStrategy(String method) {
        return paymentStrategies.stream()
                .filter(s -> s.supports(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment method: " + method));
    }
}
