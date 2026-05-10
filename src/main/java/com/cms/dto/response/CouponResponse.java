package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Integer couponId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer saleOff;
    private Integer releaseNum;
    private Integer availNum;
    private Boolean isActive;
}
