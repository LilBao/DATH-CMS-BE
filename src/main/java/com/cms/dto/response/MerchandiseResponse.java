package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseResponse {
    private Integer productId;
    private String merchName;
    private BigDecimal price;
    private Integer availNum;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imgUrl;
}
