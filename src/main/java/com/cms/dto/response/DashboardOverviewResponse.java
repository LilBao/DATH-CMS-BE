package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {
    private BigDecimal totalRevenue;
    private Long ticketsSold;
    private Long activeMovies;
    private Long totalCustomers;
    private Long totalReviews;
    private Double averageRating;
    private List<DailyRevenueResponse> revenueTrends;
    private Double seatOccupancy;
    private List<OrderResponse> recentOrders;
    private List<MovieResponse> latestMovies;
}
