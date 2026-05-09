package com.cms.repository.screening;

import com.cms.dto.response.OccupancyResponse;
import com.cms.entity.screening.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    @Query("SELECT s FROM Showtime s WHERE s.movie.slug = :slug")
    List<Showtime> findByMovieMovieSlug(String slug);

    List<Showtime> findByDayBetween(LocalDate from, LocalDate to);

    @Query("SELECT s FROM Showtime s WHERE s.movie.movieId = :movieId AND s.day = :day")
    List<Showtime> findByMovieAndDay(
            @Param("movieId") Integer movieId,
            @Param("day") LocalDate day);

    @Query("SELECT s FROM Showtime s WHERE s.screenRoom.id.branchId = :branchId AND s.day = :day")
    List<Showtime> findByBranchAndDay(
            @Param("branchId") Integer branchId,
            @Param("day") LocalDate day);

    @Query("SELECT s FROM Showtime s WHERE s.movie.movieId = :movieId " +
           "AND s.screenRoom.id.branchId = :branchId AND s.day = :day")
    List<Showtime> findByMovieBranchAndDay(
            @Param("movieId") Integer movieId,
            @Param("branchId") Integer branchId,
            @Param("day") LocalDate day);

    boolean existsByScreenRoomIdBranchIdAndScreenRoomIdRoomIdAndDayAndStartTime(
            Integer branchId, Integer roomId, LocalDate day, java.time.LocalTime startTime);

    @Query("SELECT new com.cms.dto.response.OccupancyResponse(s.timeId, m.mName, b.bName, sr.id.roomId, s.day, s.startTime, sr.rCapacity, SIZE(s.tickets)) " +
           "FROM Showtime s JOIN s.movie m JOIN s.screenRoom sr JOIN sr.branch b " +
           "WHERE s.day BETWEEN :startDate AND :endDate")
    List<OccupancyResponse> getOccupancyRates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
