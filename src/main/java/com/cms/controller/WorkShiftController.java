package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.WorkShiftRequest;
import com.cms.dto.response.WorkShiftResponse;
import com.cms.service.staff.WorkShiftService;
import com.cms.util.SecurityUtil;
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
@RequestMapping("${server.api-prefix}/work-shifts")
@RequiredArgsConstructor
@Tag(name = "Work Shift", description = "Các API quản lý ca làm việc của nhân viên")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả ca làm việc")
    public ResponseEntity<ApiResponse<List<WorkShiftResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(workShiftService.getAll()));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Lấy các ca làm đang áp dụng tại một chi nhánh cụ thể")
    public ResponseEntity<ApiResponse<List<WorkShiftResponse>>> getByBranch(@PathVariable Integer branchId) {
        if (SecurityUtil.isManager()) {
            branchId = SecurityUtil.getCurrentBranchId();
        }
        return ResponseEntity.ok(ApiResponse.ok(workShiftService.getByBranch(branchId)));
    }

    @GetMapping("/detail")
    @Operation(summary = "Lấy thông tin ca làm việc cụ thể")
    public ResponseEntity<ApiResponse<WorkShiftResponse>> getById(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam Integer wDate) {
        return ResponseEntity.ok(ApiResponse.ok(workShiftService.getById(startTime, endTime, wDate)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một ca làm việc")
    public ResponseEntity<ApiResponse<WorkShiftResponse>> create(@Valid @RequestBody WorkShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(workShiftService.create(request)));
    }

    @PutMapping
    @Operation(summary = "Cập nhật mô tả công việc của một ca làm")
    public ResponseEntity<ApiResponse<WorkShiftResponse>> update(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam Integer wDate,
            @Valid @RequestBody WorkShiftRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully",
                workShiftService.update(startTime, endTime, wDate, request)));
    }

    @DeleteMapping
    @Operation(summary = "Xóa thông tin ca làm việc")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam Integer wDate) {
        workShiftService.delete(startTime, endTime, wDate);
        return ResponseEntity.ok(ApiResponse.ok("WorkShift deleted successfully", null));
    }
}