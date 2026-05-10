package com.cms.repository.movie;

import com.cms.entity.movie.Review;
import com.cms.entity.movie.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    List<Review> findByMovieMovieId(Integer movieId);
    List<Review> findByMovieSlug(String slug);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) FROM Review r")
    Long countTotalReviews();

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRating();
}
