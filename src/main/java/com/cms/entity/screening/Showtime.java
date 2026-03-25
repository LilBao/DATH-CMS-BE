package com.cms.entity.screening;

import com.cms.entity.cinema.ScreenRoom;
import com.cms.entity.movie.Format;
import com.cms.entity.movie.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps to: Screening.TIME
 * Suất chiếu phim
 */
@Entity
@Table(name = "showtime",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_showtime_room_slot",
                        columnNames = {"branch_id", "room_id", "day", "start_time"}
                )
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "timeId")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id")
    private Integer timeId;

    @Column(name = "day")
    private LocalDate day;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_name")
    private Format format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "branch_id"),
            @JoinColumn(name = "room_id")
    })
    private ScreenRoom screenRoom;

    /**
     * Trạng thái: SCHEDULED, ONGOING, COMPLETED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ShowtimeStatus status = ShowtimeStatus.SCHEDULED;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Ticket> tickets = new ArrayList<>();

    public enum ShowtimeStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED
    }
}
