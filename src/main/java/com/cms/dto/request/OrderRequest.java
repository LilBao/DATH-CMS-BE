package com.cms.dto.request;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private String paymentMethod;
    private Integer couponId;
    private String customerId;
    private String orderStatus;
    
    private List<TicketRequest> tickets;
    private List<AddonItemRequest> addons;
    
    @Data
    public static class TicketRequest {
        private Integer showtimeId;
        private Integer branchId;
        private Integer roomId;
        private Integer sRow;
        private Integer sColumn;
        private BigDecimal tPrice;
    }
    
    @Data
    public static class AddonItemRequest {
        private Integer productId;
        private String pType;
        private String pName;
        private Integer quantity;
        private BigDecimal price; // Price per item
    }
}
