package com.cms.service.payment;

import com.cms.config.payment.MomoConfig;
import com.cms.dto.payment.PaymentRequest;
import com.cms.dto.payment.PaymentResponse;
import com.cms.dto.payment.PaymentCallbackResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomoStrategy implements PaymentStrategy {

    private final MomoConfig momoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String paymentMethod) {
        return "MOMO".equalsIgnoreCase(paymentMethod);
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String secretKey = momoConfig.getSecretKey();
        String orderId = partnerCode + System.currentTimeMillis();
        String requestId = orderId;
        String orderInfo = "Thanh toan don hang " + request.getOrderId();
        String redirectUrl = momoConfig.getRedirectUrl();
        String ipnUrl = momoConfig.getIpnUrl();
        String requestType = "payWithMethod";
        long amount = request.getAmount().longValue();
        String extraData = String.valueOf(request.getOrderId()); // Store original orderId

        // signature raw string format based on MoMo documentation
        String rawSignature = "accessKey=" + accessKey 
                + "&amount=" + amount 
                + "&extraData=" + extraData 
                + "&ipnUrl=" + ipnUrl 
                + "&orderId=" + orderId 
                + "&orderInfo=" + orderInfo 
                + "&partnerCode=" + partnerCode 
                + "&redirectUrl=" + redirectUrl 
                + "&requestId=" + requestId 
                + "&requestType=" + requestType;

        String signature = hmacSHA256(secretKey, rawSignature);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("partnerCode", partnerCode);
        requestBodyMap.put("partnerName", "Test");
        requestBodyMap.put("storeId", "MomoTestStore");
        requestBodyMap.put("requestId", requestId);
        requestBodyMap.put("amount", amount);
        requestBodyMap.put("orderId", orderId);
        requestBodyMap.put("orderInfo", orderInfo);
        requestBodyMap.put("redirectUrl", redirectUrl);
        requestBodyMap.put("ipnUrl", ipnUrl);
        requestBodyMap.put("lang", "vi");
        requestBodyMap.put("requestType", requestType);
        requestBodyMap.put("autoCapture", true);
        requestBodyMap.put("extraData", extraData);
        requestBodyMap.put("orderGroupId", "");
        requestBodyMap.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(momoConfig.getEndpoint(), entity, String.class);
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            
            if (responseMap.get("payUrl") != null) {
                return PaymentResponse.builder()
                        .paymentUrl(responseMap.get("payUrl").toString())
                        .status("SUCCESS")
                        .message("OK")
                        .build();
            } else {
                return PaymentResponse.builder()
                        .paymentUrl(null)
                        .status("FAILED")
                        .message("Tạo đơn hàng Momo thất bại")
                        .build();
            }
        } catch (Exception e) {
            log.error("Error creating Momo payment", e);
            throw new RuntimeException("Error creating Momo payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentCallbackResponse processIPN(HttpServletRequest request) {
        String resultCode = request.getParameter("resultCode");
        String message = request.getParameter("message");
        String extraData = request.getParameter("extraData");
        String transactionId = request.getParameter("orderId");

        String rawBody = null;
        // Nếu các tham số đều null, có thể dữ liệu nằm trong JSON Body (đặc thù của Momo IPN)
        if (resultCode == null && extraData == null) {
            try {
                // Đọc body và lưu lại chuỗi thô
                byte[] bodyBytes = request.getInputStream().readAllBytes();
                rawBody = new String(bodyBytes, StandardCharsets.UTF_8);
                Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
                log.info("Momo IPN Body: {}", body);
                resultCode = String.valueOf(body.get("resultCode"));
                message = String.valueOf(body.get("message"));
                extraData = String.valueOf(body.get("extraData"));
                transactionId = String.valueOf(body.get("orderId"));
            } catch (Exception e) {
                log.error("Failed to parse Momo IPN body", e);
            }
        }

        log.info("Momo IPN result: resultCode={}, orderId(extraData)={}, transId={}", resultCode, extraData, transactionId);

        String finalRawResponse = (request.getQueryString() != null) ? request.getQueryString() : rawBody;

        if ("0".equals(resultCode)) {
            return PaymentCallbackResponse.builder()
                    .orderId(extraData)
                    .transactionId(transactionId)
                    .status("SUCCESS")
                    .message("Payment success")
                    .rawResponse(finalRawResponse)
                    .build();
        }

        return PaymentCallbackResponse.builder()
                .orderId(extraData)
                .transactionId(transactionId)
                .status("FAILED")
                .message("Payment failed: " + message)
                .rawResponse(finalRawResponse)
                .build();
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac256.init(secretKey);
            byte[] hash = hmac256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmacSHA256", e);
        }
    }
}
