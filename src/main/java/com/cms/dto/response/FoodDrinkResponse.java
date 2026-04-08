package com.cms.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDrinkResponse {
    private Integer productId;
    private String pType;
    private String pName;
    private BigDecimal price;
    private Integer quantity;
    private String itemType;
}
