package com.cms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenRoomResponse {
    private Integer branchId;
    private Integer roomId;
    private String rType;
    private Integer rCapacity;
    private Integer totalSeats;
}
