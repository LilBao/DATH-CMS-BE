package com.cms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeRequest {

    @NotNull(message = "Movie ID is required")
    private Integer movieId;

    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    @NotNull(message = "Room ID is required")
    private Integer roomId;

    private String formatName;

    @NotNull(message = "Day is required")
    private LocalDate day;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}
