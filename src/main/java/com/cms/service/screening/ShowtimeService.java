package com.cms.service.screening;

import com.cms.dto.request.ShowtimeRequest;
import com.cms.dto.response.ShowtimeResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    List<ShowtimeResponse> getAll();
    ShowtimeResponse getById(Integer id);
    List<ShowtimeResponse> getByMovieAndDay(Integer movieId, LocalDate day);
    List<ShowtimeResponse> getByBranchAndDay(Integer branchId, LocalDate day);
    List<ShowtimeResponse> getByMovie(String slug);
    ShowtimeResponse create(ShowtimeRequest request);
    ShowtimeResponse update(Integer id, ShowtimeRequest request);
    ShowtimeResponse updateStatus(Integer id, String status);
    void delete(Integer id);
}
