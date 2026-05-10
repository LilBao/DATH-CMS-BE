package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.FoodDrinkRequest;
import com.cms.dto.response.FoodDrinkResponse;
import com.cms.service.products.FoodDrinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/food-drinks")
@RequiredArgsConstructor
@Tag(name = "Food & Drink", description = "Các API quản lý sản phẩm (Bắp, nước...)")
public class FoodDrinkController {

    private final FoodDrinkService foodDrinkService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả sản phẩm")
    public ResponseEntity<ApiResponse<List<FoodDrinkResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(foodDrinkService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin sản phẩm theo ID")
    public ResponseEntity<ApiResponse<FoodDrinkResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(foodDrinkService.getById(id)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lấy danh sách sản phẩm theo loại (loại bắp, loại nước...)")
    public ResponseEntity<ApiResponse<List<FoodDrinkResponse>>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok(foodDrinkService.getByType(type)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm sản phẩm theo tên")
    public ResponseEntity<ApiResponse<List<FoodDrinkResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(foodDrinkService.searchByName(name)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một sản phẩm")
    public ResponseEntity<ApiResponse<FoodDrinkResponse>> create(@Valid @RequestBody FoodDrinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(foodDrinkService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin sản phẩm")
    public ResponseEntity<ApiResponse<FoodDrinkResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody FoodDrinkRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", foodDrinkService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một sản phẩm")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        foodDrinkService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }
}
