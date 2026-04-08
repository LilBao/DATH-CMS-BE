package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.MovieRequest;
import com.cms.dto.response.MovieResponse;
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
@RequestMapping("${server.api-prefix}/movies")
@RequiredArgsConstructor
@Tag(name = "Movie", description = "Các API quản lý phim")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả phim")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin phim theo ID")
    public ResponseEntity<ApiResponse<MovieResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getById(id)));
    }

    @GetMapping("/now-showing")
    @Operation(summary = "Lấy danh sách phim đang chiếu")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNowShowing() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getNowShowing()));
    }

    @GetMapping("/coming-soon")
    @Operation(summary = "Lấy danh sách phim sắp chiếu")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getComingSoon() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getComingSoon()));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm phim theo tên")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.searchByName(name)));
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "Lấy danh sách phim theo thể loại")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getByGenre(genre)));
    }

    @PostMapping
    @Operation(summary = "Tạo mới một bộ phim")
    public ResponseEntity<ApiResponse<MovieResponse>> create(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(movieService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin phim")
    public ResponseEntity<ApiResponse<MovieResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Updated successfully", movieService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một bộ phim")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Movie deleted successfully", null));
    }
}
