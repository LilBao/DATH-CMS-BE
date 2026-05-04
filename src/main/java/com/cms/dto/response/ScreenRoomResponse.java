package com.cms.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenRoomResponse {
    private Integer branchId;
    private Integer roomId;
    private String rType;
    private Integer rCapacity;
    private BigDecimal basePrice;
    private Integer totalSeats;
}
