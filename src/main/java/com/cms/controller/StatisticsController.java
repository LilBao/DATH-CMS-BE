package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.response.BranchRevenueResponse;
import com.cms.dto.response.DailyRevenueResponse;
import com.cms.dto.response.MovieRevenueResponse;
import com.cms.dto.response.OccupancyResponse;
import com.cms.service.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/revenue/daily")
    @Operation(summary = "Thống kê doanh thu theo ngày")
    public ResponseEntity<ApiResponse<List<DailyRevenueResponse>>> getDailyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getDailyRevenue(startDate, endDate)));
    }

    @GetMapping("/revenue/movie")
    @Operation(summary = "Thống kê doanh thu theo phim")
    public ResponseEntity<ApiResponse<List<MovieRevenueResponse>>> getMovieRevenue(
            // THÊM required = false VÀO ĐÂY
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getMovieRevenue(startDate, endDate)));
    }

    @GetMapping("/revenue/branch")
    @Operation(summary = "Thống kê doanh thu theo chi nhánh")
    public ResponseEntity<ApiResponse<List<BranchRevenueResponse>>> getBranchRevenue(
            // THÊM required = false VÀO ĐÂY
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getBranchRevenue(startDate, endDate)));
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Thống kê tỷ lệ lấp đầy phòng chiếu")
    public ResponseEntity<ApiResponse<List<OccupancyResponse>>> getOccupancyRates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getOccupancyRates(startDate, endDate)));
    }
}