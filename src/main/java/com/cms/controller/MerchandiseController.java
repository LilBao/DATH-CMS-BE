package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.MerchandiseRequest;
import com.cms.dto.response.MerchandiseResponse;
import com.cms.service.products.MerchandiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/merchandise")
@RequiredArgsConstructor
@Tag(name = "Merchandise", description = "Các API quản lý quà tặng, vật phẩm lưu niệm")
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả vật phẩm")
    public ResponseEntity<ApiResponse<List<MerchandiseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(merchandiseService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin vật phẩm theo ID")
    public ResponseEntity<ApiResponse<MerchandiseResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(merchandiseService.getById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm vật phẩm theo tên")
    public ResponseEntity<ApiResponse<List<MerchandiseResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(merchandiseService.searchByName(name)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một vật phẩm")
    public ResponseEntity<ApiResponse<MerchandiseResponse>> create(@Valid @RequestBody MerchandiseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(merchandiseService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin vật phẩm")
    public ResponseEntity<ApiResponse<MerchandiseResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MerchandiseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", merchandiseService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một vật phẩm")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        merchandiseService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Merchandise deleted successfully", null));
    }
}
