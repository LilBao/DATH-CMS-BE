package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.dto.request.ReviewRequest;
import com.cms.dto.response.ReviewResponse;
import com.cms.security.UserPrincipal;
import com.cms.service.movie.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.api-prefix}/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Các API đánh giá phim")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Gửi đánh giá phim", description = "Chỉ cho phép khách hàng đã mua vé xem phim này mới được đánh giá.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ReviewRequest request) {
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Bạn cần đăng nhập để thực hiện đánh giá."));
        }

        ReviewResponse response = reviewService.createReview(currentUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Lấy danh sách đánh giá theo ID phim")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByMovieId(@PathVariable Integer movieId) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsByMovieId(movieId)));
    }

    @GetMapping("/movie/slug/{slug}")
    @Operation(summary = "Lấy danh sách đánh giá theo slug phim")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsByMovieSlug(slug)));
    }
}
