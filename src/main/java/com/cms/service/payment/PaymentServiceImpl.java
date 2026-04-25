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
import com.cms.service.email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final List<PaymentStrategy> paymentStrategies;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

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
    public PaymentCallbackResponse processIPN(HttpServletRequest httpRequest) {
        String provider = detectProvider(httpRequest);
        log.info("Detected payment provider for IPN: {}", provider);
        PaymentStrategy strategy = getStrategy(provider);
        PaymentCallbackResponse response = strategy.processIPN(httpRequest);
        return handleCommonPaymentLogic(response, httpRequest);
    }

    private String detectProvider(HttpServletRequest httpRequest) {
        String provider = httpRequest.getParameter("provider");
        if (provider != null && !provider.isEmpty()) return provider;

        // Kiểm tra Content-Type để phát hiện JSON (đặc trưng của Momo IPN)
        String contentType = httpRequest.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            return "MOMO";
        }

        if (httpRequest.getParameter("partnerCode") != null) {
            return "MOMO";
        }
        
        if (httpRequest.getParameter("vnp_TxnRef") != null || httpRequest.getParameter("vnp_ResponseCode") != null) {
            return "VNPAY";
        }

        return "VNPAY"; // Default
    }

    private PaymentCallbackResponse handleCommonPaymentLogic(PaymentCallbackResponse response, HttpServletRequest httpRequest) {
        String orderIdStr = response.getOrderId();
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            String vnpOrderInfo = httpRequest.getParameter("vnp_OrderInfo");
            if (vnpOrderInfo != null && vnpOrderInfo.startsWith("Thanh toan don hang ")) {
                orderIdStr = vnpOrderInfo.substring("Thanh toan don hang ".length());
                response.setOrderId(orderIdStr);
            }
        }
        
        if (orderIdStr != null && !orderIdStr.isEmpty()) {
            try {
                Integer orderId = Integer.parseInt(orderIdStr);
                log.info("Processing common logic for Order ID: {}", orderId);
                Optional<Payment> optionalPayment = paymentRepository.findByOrder_OrderId(orderId);
                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    
                    EPaymentStatus newStatus = "SUCCESS".equalsIgnoreCase(response.getStatus()) 
                            ? EPaymentStatus.COMPLETED 
                            : EPaymentStatus.FAILED;
                    
                    log.info("Updating Payment status for order {}: {} -> {}", orderId, payment.getPaymentStatus(), newStatus);
                            
                    payment.setPaymentStatus(newStatus);
                    if (response.getTransactionId() != null) {
                        payment.setTransactionId(response.getTransactionId());
                    }
                    paymentRepository.save(payment);

                    if (newStatus == EPaymentStatus.COMPLETED) {
                        Order pOrder = payment.getOrder();
                        if (pOrder != null) {
                            log.info("Updating Order status to PAID for Order ID: {}", orderId);
                            pOrder.setOrderStatus(EOrderStatus.PAID);
                            if (pOrder.getCustomer() != null && pOrder.getCustomer().getMembership() != null) {
                                Membership membership = pOrder.getCustomer().getMembership();
                                int earnedPoints = payment.getAmount().divide(new BigDecimal(1000)).intValue();
                                membership.setPoint(membership.getPoint() + earnedPoints);
                            }
                            orderRepository.save(pOrder);
                            
                            // Send confirmation email
                            String emailTo = pOrder.getCustomer() != null ? pOrder.getCustomer().getEmail() : 
                                            (pOrder.getEmployee() != null ? pOrder.getEmployee().getEmail() : null);
                            if (emailTo != null && !emailTo.isEmpty()) {
                                log.info("Sending confirmation email to: {}", emailTo);
                                emailService.sendOrderConfirmationEmail(emailTo, pOrder.getOrderId());
                            }
                        }
                    }

                    PaymentHistory history = PaymentHistory.builder()
                            .payment(payment)
                            .paymentStatus(newStatus)
                            .amount(payment.getAmount())
                            .transactionId(response.getTransactionId())
                            .responseCode(httpRequest.getParameter("resultCode") != null ? httpRequest.getParameter("resultCode") : httpRequest.getParameter("vnp_ResponseCode"))
                            .responseMessage(response.getMessage())
                            .rawResponse(response.getRawResponse())
                            .build();
                    paymentHistoryRepository.save(history);
                } else {
                    log.warn("Payment not found for Order ID: {}", orderId);
                }
            } catch (NumberFormatException e) {
                log.error("Invalid Order ID format: {}", orderIdStr);
            }
        } else {
            log.warn("No Order ID found in payment response");
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
