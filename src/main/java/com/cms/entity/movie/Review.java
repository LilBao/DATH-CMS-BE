package com.cms.entity.movie;

import com.cms.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps to: Movie.REVIEW
 * Composite PK: (MovieID, CUserID)
 * Rating: 1-10
 */
@Entity
@Table(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Review {

    @EmbeddedId
    private ReviewId id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "r_date", nullable = false)
    private LocalDate rDate;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cUserId")
    @JoinColumn(name = "c_user_id")
    private Customer customer;
}
