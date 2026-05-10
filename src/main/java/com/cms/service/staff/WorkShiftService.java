package com.cms.service.staff;

import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.WorkShiftResponse;

import java.time.LocalTime;
import java.util.List;

public interface WorkShiftService {
    List<WorkShiftResponse> getAll();
    List<WorkShiftResponse> getByBranch(Integer branchId);
    WorkShiftResponse getById(LocalTime startTime, LocalTime endTime, Integer wDate);
    WorkShiftResponse create(WorkShiftRequest request);
    WorkShiftResponse update(LocalTime startTime, LocalTime endTime, Integer wDate, WorkShiftRequest request);
    void delete(LocalTime startTime, LocalTime endTime, Integer wDate);
}