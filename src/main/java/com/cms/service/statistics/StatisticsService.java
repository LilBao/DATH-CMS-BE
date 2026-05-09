package com.cms.service.statistics;

import com.cms.dto.response.BranchRevenueResponse;
import com.cms.dto.response.DailyRevenueResponse;
import com.cms.dto.response.MovieRevenueResponse;
import com.cms.dto.response.OccupancyResponse;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    List<DailyRevenueResponse> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    List<MovieRevenueResponse> getMovieRevenue(LocalDate startDate, LocalDate endDate);
    List<BranchRevenueResponse> getBranchRevenue(LocalDate startDate, LocalDate endDate);
    List<OccupancyResponse> getOccupancyRates(LocalDate startDate, LocalDate endDate);
}
