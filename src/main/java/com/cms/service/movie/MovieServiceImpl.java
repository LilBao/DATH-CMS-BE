package com.cms.service.movie;

import com.cms.common.exception.AppException;
import com.cms.dto.request.MovieRequest;
import com.cms.dto.response.MovieResponse;
import com.cms.entity.movie.*;
import com.cms.repository.movie.*;
import com.cms.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private MovieResponse toResponse(Movie movie) {
        MovieResponse response = modelMapper.map(movie, MovieResponse.class);
        response.setGenres(movie.getGenres().stream().map(Genre::getGenre).collect(Collectors.toSet()));
        response.setFormats(movie.getFormats().stream().map(Format::getFName).collect(Collectors.toSet()));
        response.setActors(movie.getActors().stream().map(Actor::getFullName).collect(Collectors.toSet()));
        return response;
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        modelMapper.map(request, movie);
        movie.setMovieId(null); // Ensure ID is not overwritten from request if it exists

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
    public List<MovieResponse> getAll() {
        return movieRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Movie", id));
        return toResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getNowShowing() {
        return movieRepository.findNowShowing(LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    public List<MovieResponse> getByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public MovieResponse getBySlug(String slug) {
        Movie movie = movieRepository.findBySlug(slug);
        if (movie == null) {
            throw AppException.notFound("Movie", slug);
        }
        return toResponse(movie);
    }

    // ── Mutation ───────────────────────────────────────────────

    @Override
    public MovieResponse create(MovieRequest request) {
        Movie movie = Movie.builder().build();
        applyRequest(movie, request);
        movie.setSlug(CommonUtil.generateUniqueSlug(movie.getMName()));
        return toResponse(movieRepository.save(movie));
    }

    @Override
    public MovieResponse update(Integer id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Movie", id));
        applyRequest(movie, request);
        return toResponse(movieRepository.save(movie));
    }

    @Override
    public void delete(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw AppException.notFound("Movie", id);
        }
        movieRepository.deleteById(id);
    }
}
