package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.EmployeeRequest;
import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.EmployeeResponse;
import com.cms.service.staff.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/employees")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Các API quản lý nhân viên")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả nhân viên")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhân viên theo ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getById(id)));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Lấy danh sách nhân viên theo chi nhánh")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getByBranch(@PathVariable Integer branchId) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getByBranch(branchId)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một nhân viên")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(employeeService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", employeeService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Ngừng kích hoạt tài khoản nhân viên")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        employeeService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deactivated", null));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt tài khoản nhân viên")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable String id) {
        employeeService.activate(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee activated", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông tin nhân viên")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully", null));
    }

    @PutMapping("/{id}/work-shifts")
    @Operation(summary = "Phân ca làm việc cho nhân viên")
    public ResponseEntity<ApiResponse<Void>> assignWorkShifts(
            @PathVariable String id,
            @RequestBody List<WorkShiftRequest> requests) {
        employeeService.assignWorkShifts(id, requests);
        return ResponseEntity.ok(ApiResponse.ok("Phân ca thành công", null));
    }

    @DeleteMapping("/{id}/work-shifts")
    @Operation(summary = "Gỡ 1 ca làm việc của 1 nhân viên cụ thể")
    public ResponseEntity<ApiResponse<Void>> unassignWorkShift(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam Integer wDate) {

        employeeService.unassignWorkShift(id, startTime, endTime, wDate);
        return ResponseEntity.ok(ApiResponse.ok("Gỡ ca làm việc thành công", null));
    }
}
