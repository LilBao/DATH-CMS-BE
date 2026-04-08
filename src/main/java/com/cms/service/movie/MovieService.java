package com.cms.service.movie;

import com.cms.dto.request.MovieRequest;
import com.cms.dto.response.MovieResponse;

import java.util.List;

public interface MovieService {
    List<MovieResponse> getAll();
    MovieResponse getById(Integer id);
    List<MovieResponse> getNowShowing();
    List<MovieResponse> getComingSoon();
    List<MovieResponse> searchByName(String name);
    List<MovieResponse> getByGenre(String genre);
    MovieResponse create(MovieRequest request);
    MovieResponse update(Integer id, MovieRequest request);
    void delete(Integer id);
}
