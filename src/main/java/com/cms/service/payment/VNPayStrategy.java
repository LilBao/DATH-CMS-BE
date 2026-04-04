package com.cms.service.payment;

import com.cms.config.payment.VNPayConfig;
import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.cms.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VNPayStrategy implements PaymentStrategy {

    private final VNPayConfig vnPayConfig;

    @Override
    public boolean supports(String paymentMethod) {
        return "VNPAY".equalsIgnoreCase(paymentMethod);
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {

        long amount = request.getAmount().multiply(new BigDecimal("100")).longValue();

        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();

        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(httpRequest));
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + request.getOrderId());

        // SORT PARAMS
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnpParams.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {

                hashData.append(fieldName)
                        .append("=")
                        .append(fieldValue);

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (i < fieldNames.size() - 1) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }

        String vnpSecureHash = VNPayUtil.hmacSHA512(
                vnPayConfig.getSecretKey(),
                hashData.toString()
        );

        String paymentUrl = vnPayConfig.getVnpPayUrl()
                + "?"
                + query
                + "&vnp_SecureHash=" + vnpSecureHash;

        return PaymentResponse.builder()
                .paymentUrl(paymentUrl)
                .status("SUCCESS")
                .message("OK")
                .build();
    }

    @Override
    public PaymentCallbackResponse processCallback(HttpServletRequest request) {

        Map<String, String> fields = new HashMap<>();

        Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnpSecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = fields.get(name);

            hashData.append(name).append("=").append(value);

            if (i < fieldNames.size() - 1) {
                hashData.append("&");
            }
        }

        String signValue = VNPayUtil.hmacSHA512(
                vnPayConfig.getSecretKey(),
                hashData.toString()
        );

        if (!signValue.equals(vnpSecureHash)) {
            return PaymentCallbackResponse.builder()
                    .status("FAILED")
                    .message("Invalid checksum")
                    .build();
        }

        if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
            return PaymentCallbackResponse.builder()
                    .status("SUCCESS")
                    .message("Payment success")
                    .build();
        }

        return PaymentCallbackResponse.builder()
                .status("FAILED")
                .message("Payment failed")
                .build();
    }
}
