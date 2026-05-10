package com.cms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenRoomRequest {

    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    @NotNull(message = "Room ID is required")
    private Integer roomId;

    @NotBlank(message = "Room type is required")
    @Size(max = 30)
    private String rType;

    @NotNull(message = "Capacity is required")
    @Min(value = 1)
    private Integer rCapacity;

    private BigDecimal basePrice;
}
