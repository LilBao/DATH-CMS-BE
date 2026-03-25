package com.cms.repository.movie;

import com.cms.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findByMNameContainingIgnoreCase(String name);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= :today AND m.closingDate >= :today")
    List<Movie> findNowShowing(@Param("today") LocalDate today);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate > :today")
    List<Movie> findComingSoon(@Param("today") LocalDate today);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.genre = :genre")
    List<Movie> findByGenre(@Param("genre") String genre);
}
