package com.cms.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Order ID is mandatory")
    private Integer orderId;
    
    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is mandatory")
    private String paymentMethod;
}
