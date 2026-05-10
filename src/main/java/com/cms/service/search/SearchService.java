package com.cms.service.search;

import com.cms.entity.search.MovieDocument;
import java.util.List;

public interface SearchService {
    void syncAllMovies();
    void syncMovie(Integer movieId);
    void deleteMovie(Integer movieId);
    
    List<MovieDocument> searchMovies(String query);
    List<MovieDocument> searchMoviesByGenre(String genre);
    List<MovieDocument> searchMoviesByActor(String actorName);

    void syncAllActors();
    void syncAllDirectors();
    void syncAllGenres();
}
