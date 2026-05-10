package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.BranchRequest;
import com.cms.dto.request.ScreenRoomRequest;
import com.cms.dto.response.BranchResponse;
import com.cms.dto.response.MovieResponse;
import com.cms.dto.response.ScreenRoomResponse;
import com.cms.service.cinema.BranchService;
import com.cms.service.cinema.ScreenRoomService;
import com.cms.service.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/branches")
@RequiredArgsConstructor
@Tag(name = "Branch", description = "Các API quản lý chi nhánh và phòng chiếu")
public class BranchController {

    private final BranchService branchService;
    private final ScreenRoomService screenRoomService;
    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả chi nhánh")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi nhánh theo ID")
    public ResponseEntity<ApiResponse<BranchResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm chi nhánh theo tên")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.searchByName(name)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một chi nhánh")
    public ResponseEntity<ApiResponse<BranchResponse>> create(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(branchService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin chi nhánh")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", branchService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một chi nhánh")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted successfully", null));
    }

    @GetMapping("/{branchId}/rooms")
    @Operation(summary = "Lấy danh sách tất cả phòng chiếu của một chi nhánh")
    public ResponseEntity<ApiResponse<List<ScreenRoomResponse>>> getRooms(@PathVariable Integer branchId) {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.getByBranch(branchId)));
    }

    @GetMapping("/{branchId}/now-showing")
    @Operation(summary = "Lấy danh sách phim đang chiếu tại một chi nhánh cụ thể")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNowShowingAtBranch(@PathVariable Integer branchId) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getNowShowingAtBranch(branchId)));
    }

    @GetMapping("/{branchId}/rooms/{roomId}")
    @Operation(summary = "Lấy thông tin chi tiết phòng chiếu")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> getRoom(
            @PathVariable Integer branchId, @PathVariable Integer roomId) {
        return ResponseEntity.ok(ApiResponse.ok(screenRoomService.getById(branchId, roomId)));
    }

    @PostMapping("/{branchId}/rooms")
    @Operation(summary = "Tạo mới một phòng chiếu trong chi nhánh")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> createRoom(
            @PathVariable Integer branchId,
            @Valid @RequestBody ScreenRoomRequest request) {
        request.setBranchId(branchId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(screenRoomService.create(request)));
    }

    @PutMapping("/{branchId}/rooms/{roomId}")
    @Operation(summary = "Cập nhật thông tin phòng chiếu")
    public ResponseEntity<ApiResponse<ScreenRoomResponse>> updateRoom(
            @PathVariable Integer branchId,
            @PathVariable Integer roomId,
            @Valid @RequestBody ScreenRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully",
                screenRoomService.update(branchId, roomId, request)));
    }

    @DeleteMapping("/{branchId}/rooms/{roomId}")
    @Operation(summary = "Xóa một phòng chiếu")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Integer branchId, @PathVariable Integer roomId) {
        screenRoomService.delete(branchId, roomId);
        return ResponseEntity.ok(ApiResponse.ok("Room deleted successfully", null));
    }
}
