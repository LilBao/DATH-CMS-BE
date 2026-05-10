package com.cms.service.statistics;

import com.cms.dto.response.*;
import com.cms.repository.booking.OrderRepository;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.movie.MovieRepository;
import com.cms.repository.screening.ShowtimeRepository;
import com.cms.repository.movie.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CustomerRepository customerRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<DailyRevenueResponse> getDailyRevenue(LocalDate startDate, LocalDate endDate, Integer branchId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getDailyRevenue(start, end, branchId);
    }

    @Override
    public List<MovieRevenueResponse> getMovieRevenue(LocalDate startDate, LocalDate endDate, Integer branchId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getMovieRevenue(start, end, branchId);
    }

    @Override
    public List<BranchRevenueResponse> getBranchRevenue(LocalDate startDate, LocalDate endDate, Integer branchId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return orderRepository.getBranchRevenue(start, end, branchId);
    }

    @Override
    public List<OccupancyResponse> getOccupancyRates(LocalDate startDate, LocalDate endDate, Integer branchId) {
        List<OccupancyResponse> rates = showtimeRepository.getOccupancyRates(startDate, endDate, branchId);
        rates.forEach(rate -> {
            if (rate.getCapacity() != null && rate.getCapacity() > 0) {
                rate.setOccupancyRate((double) rate.getTicketsSold() / rate.getCapacity() * 100);
            }
        });
        return rates;
    }

    @Override
    public DashboardOverviewResponse getDashboardOverview() {
        return getDashboardOverview(null);
    }

    public DashboardOverviewResponse getDashboardOverview(Integer branchId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.minusDays(6).atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.getTotalRevenue(branchId);
        Long ticketsSold = orderRepository.getTicketsSold(branchId);
        Long activeMovies = (long) movieRepository.findNowShowing(today).size();
        Long totalCustomers = customerRepository.count();
        Long totalReviews = reviewRepository.countTotalReviews();
        Double averageRating = reviewRepository.getAverageRating();

        List<DailyRevenueResponse> revenueTrends = orderRepository.getDailyRevenue(startOfWeek, endOfDay, branchId);
        
        List<OccupancyResponse> occupancyRates = showtimeRepository.getOccupancyRates(today, today, branchId);
        Double avgOccupancy = occupancyRates.stream()
                .mapToDouble(r -> r.getCapacity() > 0 ? (double) r.getTicketsSold() / r.getCapacity() * 100 : 0)
                .average().orElse(0.0);

        List<OrderResponse> recentOrders = orderRepository.findRecentOrders(branchId, org.springframework.data.domain.PageRequest.of(0, 5))
                .stream().map(o -> {
                    OrderResponse res = modelMapper.map(o, OrderResponse.class);
                    res.setOrderStatus(o.getOrderStatus().name());
                    res.setTotal(o.getTotal());
                    
                    if (o.getCustomer() != null && res.getCustomer() != null) {
                        res.getCustomer().setAvatarUrl(o.getCustomer().getAvatarUrl());
                    }
                    
                    if (o.getTickets() != null) {
                        res.setTicketDetails(o.getTickets().stream().map(t -> {
                            TicketResponse tr = new TicketResponse();
                            tr.setTicketId(t.getTicketId());
                            if (t.getShowtime() != null && t.getShowtime().getMovie() != null) {
                                tr.setMovieName(t.getShowtime().getMovie().getMName());
                            }
                            tr.setPrice(t.getTPrice());
                            return tr;
                        }).collect(Collectors.toList()));
                    }
                    
                    return res;
                }).collect(Collectors.toList());

        List<MovieResponse> latestMovies = movieRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("releaseDate").descending()))
                .stream().map(m -> {
                    MovieResponse res = modelMapper.map(m, MovieResponse.class);
                    res.setGenres(m.getGenres().stream().map(g -> g.getGenre()).collect(Collectors.toSet()));
                    res.setFormats(m.getFormats().stream().map(f -> f.getFName()).collect(Collectors.toSet()));
                    res.setActors(m.getActors().stream().map(a -> a.getFullName()).collect(Collectors.toSet()));
                    return res;
                }).collect(Collectors.toList());

        return DashboardOverviewResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .ticketsSold(ticketsSold != null ? ticketsSold : 0L)
                .activeMovies(activeMovies)
                .totalCustomers(totalCustomers)
                .totalReviews(totalReviews != null ? totalReviews : 0L)
                .averageRating(averageRating != null ? (Math.round(averageRating * 10.0) / 10.0) : 0.0)
                .revenueTrends(revenueTrends)
                .seatOccupancy(Math.round(avgOccupancy * 10.0) / 10.0)
                .recentOrders(recentOrders)
                .latestMovies(latestMovies)
                .build();
    }
}
