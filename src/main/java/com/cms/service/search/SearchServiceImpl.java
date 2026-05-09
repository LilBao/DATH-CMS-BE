package com.cms.service.search;

import com.cms.entity.movie.*;
import com.cms.entity.search.*;
import com.cms.repository.movie.*;
import com.cms.repository.search.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final MovieRepository movieRepository;
    private final MovieSearchRepository movieSearchRepository;
    
    private final ActorRepository actorRepository;
    private final ActorSearchRepository actorSearchRepository;
    
    private final DirectorRepository directorRepository;
    private final DirectorSearchRepository directorSearchRepository;
    
    private final GenreRepository genreRepository;
    private final GenreSearchRepository genreSearchRepository;

    @Override
    @Transactional(readOnly = true)
    public void syncAllMovies() {
        log.info("Starting synchronization of all movies to Elasticsearch");
        List<Movie> movies = movieRepository.findAll();
        List<MovieDocument> documents = movies.stream()
                .map(this::mapToMovieDocument)
                .collect(Collectors.toList());
        movieSearchRepository.saveAll(documents);
        log.info("Successfully synchronized {} movies to Elasticsearch", documents.size());
    }

    @Override
    @Transactional(readOnly = true)
    public void syncMovie(Integer movieId) {
        movieRepository.findById(movieId).ifPresent(movie -> {
            movieSearchRepository.save(mapToMovieDocument(movie));
            log.info("Synchronized movie ID {} to Elasticsearch", movieId);
        });
    }

    @Override
    public void deleteMovie(Integer movieId) {
        movieSearchRepository.deleteById(String.valueOf(movieId));
        log.info("Deleted movie ID {} from Elasticsearch", movieId);
    }

    @Override
    public List<MovieDocument> searchMovies(String query) {
        return movieSearchRepository.findByTitleContainingIgnoreCase(query);
    }

    @Override
    public List<MovieDocument> searchMoviesByGenre(String genre) {
        // Simple search by genre, can be improved with more complex queries
        return movieSearchRepository.findByTitleContainingIgnoreCase(genre); 
        // Note: Ideally should use a custom query for list of genres
    }

    @Override
    public List<MovieDocument> searchMoviesByActor(String actorName) {
        return movieSearchRepository.findByTitleContainingIgnoreCase(actorName);
    }

    @Override
    @Transactional(readOnly = true)
    public void syncAllActors() {
        List<Actor> actors = actorRepository.findAll();
        List<ActorDocument> documents = actors.stream()
                .map(a -> ActorDocument.builder()
                        .id(a.getFullName()) // Using name as ID for simplicity or you might have an ID
                        .fullName(a.getFullName())
                        .build())
                .collect(Collectors.toList());
        actorSearchRepository.saveAll(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public void syncAllDirectors() {
        List<Director> directors = directorRepository.findAll();
        List<DirectorDocument> documents = directors.stream()
                .map(d -> DirectorDocument.builder()
                        .id(d.getFullName())
                        .fullName(d.getFullName())
                        .build())
                .collect(Collectors.toList());
        directorSearchRepository.saveAll(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public void syncAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        List<GenreDocument> documents = genres.stream()
                .map(g -> GenreDocument.builder()
                        .id(g.getGenre())
                        .genreName(g.getGenre())
                        .build())
                .collect(Collectors.toList());
        genreSearchRepository.saveAll(documents);
    }

    private MovieDocument mapToMovieDocument(Movie movie) {
        return MovieDocument.builder()
                .id(String.valueOf(movie.getMovieId()))
                .title(movie.getMName())
                .description(movie.getDescript())
                .runTime(movie.getRunTime())
                .releaseDate(movie.getReleaseDate())
                .genres(movie.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                .actors(movie.getActors().stream().map(Actor::getFullName).collect(Collectors.toList()))
                .formats(movie.getFormats().stream().map(Format::getFName).collect(Collectors.toList()))
                .ageRating(movie.getAgeRating())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
