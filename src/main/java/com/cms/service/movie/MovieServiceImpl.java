package com.cms.service.movie;

import com.cms.common.exception.AppException;
import com.cms.dto.request.MovieRequest;
import com.cms.dto.response.MovieResponse;
import com.cms.entity.movie.*;
import com.cms.repository.movie.*;
import com.cms.service.search.SearchService;
import com.cms.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final FormatRepository formatRepository;
    private final ActorRepository actorRepository;
    private final ModelMapper modelMapper;
//    TODO
//    private final SearchService searchService;

    private MovieResponse toResponse(Movie movie) {
        MovieResponse response = modelMapper.map(movie, MovieResponse.class);
        response.setGenres(movie.getGenres().stream().map(Genre::getGenre).collect(Collectors.toSet()));
        response.setFormats(movie.getFormats().stream().map(Format::getFName).collect(Collectors.toSet()));
        response.setActors(movie.getActors().stream().map(Actor::getFullName).collect(Collectors.toSet()));

        Set<Review> reviews = movie.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            response.setAvgRating(Math.round(avg * 10.0) / 10.0);
            response.setReviewCount(reviews.size());
        } else {
            response.setAvgRating(0.0);
            response.setReviewCount(0);
        }
        return response;
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        modelMapper.map(request, movie);
        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            movie.setGenres(genres);
        }
        if (request.getFormatIds() != null) {
            Set<Format> formats = new HashSet<>(formatRepository.findAllById(request.getFormatIds()));
            movie.setFormats(formats);
        }
        if (request.getActorIds() != null) {
            Set<Actor> actors = new HashSet<>(actorRepository.findAllById(request.getActorIds()));
            movie.setActors(actors);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("movies")
    public List<MovieResponse> getAll() {
        return movieRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "movies", key = "#id")
    public MovieResponse getById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Movie", id));
        return toResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("movies_now_showing")
    public List<MovieResponse> getNowShowing() {
        return movieRepository.findNowShowing(LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "movies_now_showing_branch", key = "#branchId")
    public List<MovieResponse> getNowShowingAtBranch(Integer branchId) {
        return movieRepository.findNowShowingAtBranch(branchId, LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("movies_coming_soon")
    public List<MovieResponse> getComingSoon() {
        return movieRepository.findComingSoon(LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> searchByName(String name) {
        return movieRepository.findByMNameContainingIgnoreCase(name).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "movies_genre", key = "#genre")
    public List<MovieResponse> getByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "movies_slug", key = "#slug")
    public MovieResponse getBySlug(String slug) {
        Movie movie = movieRepository.findBySlug(slug);
        if (movie == null) {
            throw AppException.notFound("Movie", slug);
        }
        return toResponse(movie);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true),
            @CacheEvict(value = "movies_now_showing", allEntries = true),
            @CacheEvict(value = "movies_now_showing_branch", allEntries = true),
            @CacheEvict(value = "movies_coming_soon", allEntries = true),
            @CacheEvict(value = "movies_slug", allEntries = true),
            @CacheEvict(value = "movies_genre", allEntries = true)
    })
    public MovieResponse create(MovieRequest request) {
        Movie movie = Movie.builder().build();
        applyRequest(movie, request);
        movie.setSlug(CommonUtil.generateUniqueSlug(movie.getMName()));
        Movie savedMovie = movieRepository.save(movie);
        //searchService.syncMovie(savedMovie.getMovieId());
        return toResponse(savedMovie);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true),
            @CacheEvict(value = "movies_now_showing", allEntries = true),
            @CacheEvict(value = "movies_coming_soon", allEntries = true),
            @CacheEvict(value = "movies_slug", allEntries = true),
            @CacheEvict(value = "movies_genre", allEntries = true)
    })
    public MovieResponse update(Integer id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Movie", id));
        applyRequest(movie, request);
        Movie updatedMovie = movieRepository.save(movie);
        //searchService.syncMovie(updatedMovie.getMovieId());
        return toResponse(updatedMovie);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true),
            @CacheEvict(value = "movies_now_showing", allEntries = true),
            @CacheEvict(value = "movies_coming_soon", allEntries = true),
            @CacheEvict(value = "movies_slug", allEntries = true),
            @CacheEvict(value = "movies_genre", allEntries = true)
    })
    public void delete(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw AppException.notFound("Movie", id);
        }

        try {
            movieRepository.deleteById(id);
            // Ép Hibernate thực thi câu lệnh SQL ngay lập tức để bắt lỗi ForeignKey
            movieRepository.flush();
//            searchService.deleteMovie(id);
        } catch (DataIntegrityViolationException e) {
            // Ném lỗi về Controller để GlobalExceptionHandler xử lý thành phản hồi 400 Bad Request
            throw new AppException(HttpStatus.BAD_REQUEST,"", "Không thể xóa phim này vì đã có suất chiếu được xếp lịch.");
        }
    }
}
