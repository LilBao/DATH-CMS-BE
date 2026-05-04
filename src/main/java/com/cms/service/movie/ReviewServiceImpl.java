package com.cms.service.movie;

import com.cms.dto.request.ReviewRequest;
import com.cms.dto.response.ReviewResponse;
import com.cms.entity.customer.Customer;
import com.cms.entity.movie.Movie;
import com.cms.entity.movie.Review;
import com.cms.entity.movie.ReviewId;
import com.cms.enums.ETicketStatus;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.movie.MovieRepository;
import com.cms.repository.movie.ReviewRepository;
import com.cms.repository.screening.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(String userId, ReviewRequest request) {
        // 1. Check if user has bought tickets for this movie
        boolean hasBoughtTicket = ticketRepository.existsByOrderCustomer(
                userId, request.getMovieId(), Arrays.asList(ETicketStatus.SOLD, ETicketStatus.CHECKED_IN));

        if (!hasBoughtTicket) {
            throw new RuntimeException("Bạn phải mua vé xem phim này mới có thể đánh giá!");
        }

        // 2. Load Customer and Movie
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim!"));

        // 3. Create or update review
        ReviewId reviewId = new ReviewId(request.getMovieId(), userId);
        Review review = reviewRepository.findById(reviewId).orElse(new Review());
        
        review.setId(reviewId);
        review.setMovie(movie);
        review.setCustomer(customer);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setRDate(LocalDate.now());

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByMovieId(Integer movieId) {
        return reviewRepository.findByMovieMovieId(movieId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByMovieSlug(String slug) {
        return reviewRepository.findByMovieSlug(slug).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .customerName(review.getCustomer().getCName())
                .customerAvatar(review.getCustomer().getAvatarUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getRDate())
                .build();
    }
}
