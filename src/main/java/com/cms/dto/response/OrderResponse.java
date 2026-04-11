package com.cms.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Integer orderId;
    private LocalDateTime orderTime;
    private String paymentMethod;
    private BigDecimal originalTotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String orderStatus;
    private String paymentUrl;
}
