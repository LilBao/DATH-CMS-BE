package com.cms.dto.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddonResponse {
    private Integer productId;
    private String pName;
    private Integer quantity;
    private BigDecimal price;
    private String itemType;
}