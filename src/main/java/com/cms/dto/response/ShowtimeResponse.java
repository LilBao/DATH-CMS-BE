package com.cms.dto.response;

import com.cms.enums.ERType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeResponse {
    private Integer timeId;
    private Integer movieId;
    private String movieName;
    private Integer branchId;
    private String branchName;
    private Integer roomId;
    private ERType rType;
    private BigDecimal rPrice;
    private String formatName;
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}
