package com.cms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequest {
    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    @NotNull(message = "Room ID is required")
    private Integer roomId;

    @NotNull(message = "Row is required")
    private Integer sRow;

    @NotNull(message = "Column is required")
    private Integer sColumn;

    private Integer sType;
    private Boolean sStatus;
}
