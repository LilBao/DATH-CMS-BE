package com.cms.service.staff;

import com.cms.dto.request.EmployeeRequest;
import com.cms.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAll();
    EmployeeResponse getById(String id);
    List<EmployeeResponse> getByBranch(Integer branchId);
    EmployeeResponse create(EmployeeRequest request);
    EmployeeResponse update(String id, EmployeeRequest request);
    void assignWorkShifts(String employeeId, List<com.cms.dto.request.WorkShiftRequest> shiftRequests);
    void unassignWorkShift(String employeeId, java.time.LocalTime startTime, java.time.LocalTime endTime, Integer wDate);
    void deactivate(String id);
    void delete(String id);
}
