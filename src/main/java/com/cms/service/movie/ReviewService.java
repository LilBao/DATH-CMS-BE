package com.cms.service.movie;

import com.cms.dto.request.ReviewRequest;
import com.cms.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(String userId, ReviewRequest request);
    List<ReviewResponse> getReviewsByMovieId(Integer movieId);
    List<ReviewResponse> getReviewsByMovieSlug(String slug);
}
