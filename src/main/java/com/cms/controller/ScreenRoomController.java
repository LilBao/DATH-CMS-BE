package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.ScreenRoomRequest;
import com.cms.dto.response.ScreenRoomResponse;
import com.cms.service.cinema.ScreenRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/rooms")
@RequiredArgsConstructor
@Tag(name = "Screen Room", description = "Các API quản lý phòng chiếu (Global)")
public class ScreenRoomController {

    private final ScreenRoomService screenRoomService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả phòng chiếu (toàn hệ thống)")
    public ResponseEntity<ApiResponse<List<ScreenRoomResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.getAll()));
    }

    @GetMapping("/{branchId}/{roomId}")
    @Operation(summary = "Lấy thông tin chi tiết của một phòng chiếu")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> getById(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId) {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.getById(branchId, roomId)));
    }

    @PostMapping
    @Operation(summary = "Tạo phòng chiếu mới")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> create(@RequestBody ScreenRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.create(request)));
    }

    @PutMapping("/{branchId}/{roomId}")
    @Operation(summary = "Cập nhật thông tin phòng chiếu")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> update(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @RequestBody ScreenRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.update(branchId, roomId, request)));
    }

    @DeleteMapping("/{branchId}/{roomId}")
    @Operation(summary = "Xóa phòng chiếu")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId) {
        screenRoomService.delete(branchId, roomId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
