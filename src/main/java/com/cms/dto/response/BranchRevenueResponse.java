package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRevenueResponse {
    private Integer branchId;
    private String branchName;
    private BigDecimal revenue;
    private Long ticketCount;
}
