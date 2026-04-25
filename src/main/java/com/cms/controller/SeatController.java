package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.SeatRequest;
import com.cms.dto.response.SeatResponse;
import com.cms.service.cinema.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/seats")
@RequiredArgsConstructor
@Tag(name = "Seat", description = "Các API quản lý ghế ngồi trong phòng chiếu")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("branches/{branchId}/rooms/{roomId}")
    @Operation(summary = "Lấy danh sách ghế của một phòng chiếu")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeats(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.getByRoom(branchId, roomId)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một ghế")
    public ResponseEntity<ApiResponse<SeatResponse>> createSeat(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @Valid @RequestBody SeatRequest request) {
        request.setBranchId(branchId);
        request.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(seatService.create(request)));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Tạo nhiều ghế cùng lúc")
    public ResponseEntity<ApiResponse<String>> createSeatsBulk(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @RequestBody List<SeatRequest> requests) {
        for (SeatRequest request : requests) {
            request.setBranchId(branchId);
            request.setRoomId(roomId);
        }
        seatService.createBulk(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Bulk seats created successfully"));
    }

    @PutMapping("/{sRow}/{sColumn}")
    @Operation(summary = "Cập nhật thông tin ghế")
    public ResponseEntity<ApiResponse<SeatResponse>> updateSeat(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @PathVariable Integer sRow,
            @PathVariable Integer sColumn,
            @RequestBody SeatRequest request) {
        request.setBranchId(branchId);
        request.setRoomId(roomId);
        request.setSRow(sRow);
        request.setSColumn(sColumn);
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", seatService.update(request)));
    }

    @DeleteMapping("/{sRow}/{sColumn}")
    @Operation(summary = "Xóa một ghế")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @PathVariable Integer sRow,
            @PathVariable Integer sColumn) {
        seatService.delete(branchId, roomId, sRow, sColumn);
        return ResponseEntity.ok(ApiResponse.ok("Seat deleted successfully", null));
    }
}
