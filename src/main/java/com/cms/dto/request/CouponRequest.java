package com.cms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer saleOff;
    private Integer releaseNum;
    private Integer availNum;
    @Builder.Default
    private Boolean isActive = true;
}
