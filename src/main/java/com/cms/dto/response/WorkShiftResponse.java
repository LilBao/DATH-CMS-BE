package com.cms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class WorkShiftResponse {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer wDate;
    private String work;
    private List<EmployeeResponse> employees;
}