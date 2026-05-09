package com.cms.service.statistics;

import com.cms.dto.response.BranchRevenueResponse;
import com.cms.dto.response.DailyRevenueResponse;
import com.cms.dto.response.MovieRevenueResponse;
import com.cms.dto.response.OccupancyResponse;
import com.cms.repository.booking.OrderRepository;
import com.cms.repository.screening.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public List<DailyRevenueResponse> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getDailyRevenue(start, end);
    }

    @Override
    public List<MovieRevenueResponse> getMovieRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getMovieRevenue(start, end);
    }

    @Override
    public List<BranchRevenueResponse> getBranchRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getBranchRevenue(start, end);
    }

    @Override
    public List<OccupancyResponse> getOccupancyRates(LocalDate startDate, LocalDate endDate) {
        List<OccupancyResponse> rates = showtimeRepository.getOccupancyRates(startDate, endDate);
        rates.forEach(rate -> {
            if (rate.getCapacity() != null && rate.getCapacity() > 0) {
                rate.setOccupancyRate((double) rate.getTicketsSold() / rate.getCapacity() * 100);
            }
        });
        return rates;
    }
}
