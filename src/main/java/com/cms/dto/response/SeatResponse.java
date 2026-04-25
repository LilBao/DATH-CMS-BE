package com.cms.dto.response;

import com.cms.entity.cinema.SeatId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Integer branchId;
    private Integer roomId;
    private Integer sRow;
    private Integer sColumn;
    private Integer sType;
    private Boolean sStatus;
    private Boolean isBooked;
}
