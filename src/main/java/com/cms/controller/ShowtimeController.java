package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.ShowtimeRequest;
import com.cms.dto.response.ShowtimeResponse;
import com.cms.service.screening.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/showtimes")
@RequiredArgsConstructor
@Tag(name = "Showtime", description = "Các API quản lý suất chiếu")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả suất chiếu")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(showtimeService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin suất chiếu theo ID")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(showtimeService.getById(id)));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Lấy danh sách suất chiếu theo phim")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getByMovie(@PathVariable Integer movieId) {
        return ResponseEntity.ok(ApiResponse.ok(showtimeService.getByMovie(movieId)));
    }

    @GetMapping("/movie/{movieId}/day")
    @Operation(summary = "Lấy danh sách suất chiếu theo phim và ngày")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getByMovieAndDay(
            @PathVariable Integer movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return ResponseEntity.ok(ApiResponse.ok(showtimeService.getByMovieAndDay(movieId, day)));
    }

    @GetMapping("/branch/{branchId}/day")
    @Operation(summary = "Lấy danh sách suất chiếu theo chi nhánh và ngày")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getByBranchAndDay(
            @PathVariable Integer branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return ResponseEntity.ok(ApiResponse.ok(showtimeService.getByBranchAndDay(branchId, day)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một suất chiếu")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(@Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(showtimeService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin suất chiếu")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", showtimeService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái suất chiếu")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", showtimeService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một suất chiếu")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        showtimeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Showtime deleted successfully", null));
    }
}
