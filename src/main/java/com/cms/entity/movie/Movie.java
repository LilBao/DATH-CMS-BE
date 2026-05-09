package com.cms.entity.movie;

import com.cms.entity.screening.Showtime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps to: Movie.MOVIE
 */
@Entity
@Table(name = "movie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "movieId")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "m_name", length = 255, nullable = false)
    private String mName;

    @Column(name = "slug", length = 255, nullable = false)
    private String slug;

    @Column(name = "descript", columnDefinition = "TEXT")
    private String descript;

    /**
     * Thời lượng phim (phút)
     */
    @Column(name = "run_time", nullable = false)
    private Integer runTime;

    @Column(name = "is_dub", nullable = false)
    @Builder.Default
    private Boolean isDub = false;

    @Column(name = "is_sub", nullable = false)
    @Builder.Default
    private Boolean isSub = true;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    /**
     * Nhãn độ tuổi: K, T13, T16, T18
     */
    @Column(name = "age_rating", length = 10, nullable = false)
    private String ageRating;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "trailer_url", columnDefinition = "TEXT")
    private String trailerUrl;

    // ── Relationships ──────────────────────────────────────────

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_format",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "f_name")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Format> formats = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "features",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "full_name")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "directs",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "full_name")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Director> directors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Showtime> showtimes = new HashSet<>();
}
