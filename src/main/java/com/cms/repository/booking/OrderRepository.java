package com.cms.repository.booking;

import com.cms.dto.response.BranchRevenueResponse;
import com.cms.dto.response.DailyRevenueResponse;
import com.cms.dto.response.MovieRevenueResponse;
import com.cms.entity.booking.Order;
import com.cms.enums.EOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.customer c " +
           "LEFT JOIN FETCH o.tickets t " +
           "LEFT JOIN FETCH t.showtime st " +
           "LEFT JOIN FETCH st.movie m " +
           "LEFT JOIN FETCH t.seat s " +
           "LEFT JOIN FETCH s.screenRoom sr " +
           "LEFT JOIN FETCH sr.branch b " +
           "WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

    @Query("SELECT o FROM Order o WHERE o.customer.cUserId = :cUserId ORDER BY o.orderTime DESC")
    List<Order> findByCustomerCUserIdOrderByOrderTimeDesc(@Param("cUserId") String cUserId);

    List<Order> findByOrderStatus(EOrderStatus status);

    List<Order> findByCustomerEmail(String email);

    @Query("SELECT new com.cms.dto.response.DailyRevenueResponse(CAST(o.orderTime AS localdate), SUM(o.total), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND o.orderTime BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(o.orderTime AS localdate) " +
           "ORDER BY CAST(o.orderTime AS localdate)")
    List<DailyRevenueResponse> getDailyRevenue(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT new com.cms.dto.response.MovieRevenueResponse(m.movieId, m.mName, SUM(t.tPrice), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t JOIN t.showtime st JOIN st.movie m " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND o.orderTime BETWEEN :startDate AND :endDate " +
           "GROUP BY m.movieId, m.mName " +
           "ORDER BY SUM(t.tPrice) DESC")
    List<MovieRevenueResponse> getMovieRevenue(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT new com.cms.dto.response.BranchRevenueResponse(b.branchId, b.bName, SUM(t.tPrice), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t JOIN t.showtime st JOIN st.screenRoom sr JOIN sr.branch b " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND o.orderTime BETWEEN :startDate AND :endDate " +
           "GROUP BY b.branchId, b.bName " +
           "ORDER BY SUM(t.tPrice) DESC")
    List<BranchRevenueResponse> getBranchRevenue(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'PENDING' AND o.orderTime < :timeout")
    List<Order> findPendingOrdersBefore(@Param("timeout") LocalDateTime timeout);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.tickets t JOIN t.showtime st " +
           "WHERE o.orderStatus = 'PAID' AND st.day = :day " +
           "AND st.startTime BETWEEN :now AND :upcoming")
    List<Order> findPaidOrdersWithUpcomingShowtime(
            @Param("day") java.time.LocalDate day,
            @Param("now") java.time.LocalTime now,
            @Param("upcoming") java.time.LocalTime upcoming);
}
