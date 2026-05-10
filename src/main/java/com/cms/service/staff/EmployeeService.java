package com.cms.service.staff;

import com.cms.dto.request.EmployeeRequest;
import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.EmployeeResponse;

import java.time.LocalTime;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAll();
    EmployeeResponse getById(String id);
    List<EmployeeResponse> getByBranch(Integer branchId);
    EmployeeResponse create(EmployeeRequest request);
    EmployeeResponse update(String id, EmployeeRequest request);
    void assignWorkShifts(String employeeId, List<WorkShiftRequest> shiftRequests);
    void unassignWorkShift(String employeeId, LocalTime startTime, LocalTime endTime, Integer wDate);
    void deactivate(String id);
    void activate(String id);
    void delete(String id);
}
