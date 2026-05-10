package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.response.*;
import com.cms.entity.staff.Employee;
import com.cms.repository.staff.EmployeeRepository;
import com.cms.security.CurrentUser;
import com.cms.service.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Các API thống kê báo cáo")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/overview")
    @Operation(summary = "Lấy dữ liệu tổng quan cho Dashboard")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview(
            @CurrentUser UserDetails userDetails,
            @RequestParam(required = false) Integer branchId) {

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            Employee emp = employeeRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (emp != null && emp.getBranch() != null) {
                branchId = emp.getBranch().getBranchId();
            }
        }

        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getDashboardOverview(branchId)));
    }

    @GetMapping("/revenue/daily")
    @Operation(summary = "Thống kê doanh thu theo ngày")
    public ResponseEntity<ApiResponse<List<DailyRevenueResponse>>> getDailyRevenue(
            @CurrentUser UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer branchId) {

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            Employee emp = employeeRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (emp != null && emp.getBranch() != null) {
                branchId = emp.getBranch().getBranchId();
            }
        }

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getDailyRevenue(startDate, endDate, branchId)));
    }

    @GetMapping("/revenue/movie")
    @Operation(summary = "Thống kê doanh thu theo phim")
    public ResponseEntity<ApiResponse<List<MovieRevenueResponse>>> getMovieRevenue(
            @CurrentUser UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer branchId) {

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            Employee emp = employeeRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (emp != null && emp.getBranch() != null) {
                branchId = emp.getBranch().getBranchId();
            }
        }

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getMovieRevenue(startDate, endDate, branchId)));
    }

    @GetMapping("/revenue/branch")
    @Operation(summary = "Thống kê doanh thu theo chi nhánh")
    public ResponseEntity<ApiResponse<List<BranchRevenueResponse>>> getBranchRevenue(
            @CurrentUser UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer branchId) {

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            Employee emp = employeeRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (emp != null && emp.getBranch() != null) {
                branchId = emp.getBranch().getBranchId();
            }
        }

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getBranchRevenue(startDate, endDate, branchId)));
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Thống kê tỷ lệ lấp đầy phòng chiếu")
    public ResponseEntity<ApiResponse<List<OccupancyResponse>>> getOccupancyRates(
            @CurrentUser UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer branchId) {

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            Employee emp = employeeRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (emp != null && emp.getBranch() != null) {
                branchId = emp.getBranch().getBranchId();
            }
        }

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getOccupancyRates(startDate, endDate, branchId)));
    }
}