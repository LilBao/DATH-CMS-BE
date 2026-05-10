package com.cms.dto.request;

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
public class MerchandiseRequest {
    private String merchName;
    private BigDecimal price;
    private Integer availNum;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imgUrl;
}
