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

import java.math.BigDecimal;
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

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c WHERE o.orderStatus = :status")
    List<Order> findByOrderStatusWithCustomer(@Param("status") EOrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c")
    List<Order> findAllWithCustomer();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c WHERE o.customer.email = :email")
    List<Order> findByCustomerEmailWithCustomer(@Param("email") String email);

    List<Order> findByOrderStatus(EOrderStatus status);

    List<Order> findByCustomerEmail(String email);

    @Query("SELECT new com.cms.dto.response.DailyRevenueResponse(CAST(o.orderTime AS localdate), SUM(o.total), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t JOIN t.showtime st JOIN st.screenRoom sr " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND (o.orderTime BETWEEN :startDate AND :endDate) " +
           "AND (:branchId IS NULL OR sr.id.branchId = :branchId) " +
           "GROUP BY CAST(o.orderTime AS localdate) " +
           "ORDER BY CAST(o.orderTime AS localdate)")
    List<DailyRevenueResponse> getDailyRevenue(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            @Param("branchId") Integer branchId);

    @Query("SELECT new com.cms.dto.response.MovieRevenueResponse(m.movieId, m.mName, SUM(t.tPrice), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t JOIN t.showtime st JOIN st.movie m JOIN st.screenRoom sr " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND (o.orderTime BETWEEN :startDate AND :endDate) " +
           "AND (:branchId IS NULL OR sr.id.branchId = :branchId) " +
           "GROUP BY m.movieId, m.mName " +
           "ORDER BY SUM(t.tPrice) DESC")
    List<MovieRevenueResponse> getMovieRevenue(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            @Param("branchId") Integer branchId);

    @Query("SELECT new com.cms.dto.response.BranchRevenueResponse(b.branchId, b.bName, SUM(t.tPrice), COUNT(t)) " +
           "FROM Order o JOIN o.tickets t JOIN t.showtime st JOIN st.screenRoom sr JOIN sr.branch b " +
           "WHERE o.orderStatus = 'PAID' " +
           "AND (o.orderTime BETWEEN :startDate AND :endDate) " +
           "AND (:branchId IS NULL OR b.branchId = :branchId) " +
           "GROUP BY b.branchId, b.bName " +
           "ORDER BY SUM(t.tPrice) DESC")
    List<BranchRevenueResponse> getBranchRevenue(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            @Param("branchId") Integer branchId);
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'PENDING' AND o.orderTime < :timeout")
    List<Order> findPendingOrdersBefore(@Param("timeout") LocalDateTime timeout);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.tickets t JOIN t.showtime st " +
           "WHERE o.orderStatus = 'PAID' AND st.day = :day " +
           "AND st.startTime BETWEEN :now AND :upcoming")
    List<Order> findPaidOrdersWithUpcomingShowtime(
            @Param("day") java.time.LocalDate day,
            @Param("now") java.time.LocalTime now,
            @Param("upcoming") java.time.LocalTime upcoming);
    @Query("SELECT SUM(o.total) FROM Order o " +
           "WHERE o.orderStatus = 'PAID' AND (:branchId IS NULL OR o.orderId IN " +
           "(SELECT t.order.orderId FROM Ticket t JOIN t.showtime st JOIN st.screenRoom sr WHERE sr.id.branchId = :branchId))")
    BigDecimal getTotalRevenue(@Param("branchId") Integer branchId);

    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.showtime st JOIN st.screenRoom sr " +
           "WHERE t.order.orderStatus = 'PAID' AND (:branchId IS NULL OR sr.id.branchId = :branchId)")
    Long getTicketsSold(@Param("branchId") Integer branchId);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.customer c " +
           "LEFT JOIN FETCH o.tickets t " +
           "LEFT JOIN FETCH t.showtime st " +
           "LEFT JOIN FETCH st.screenRoom sr " +
           "WHERE (:status IS NULL OR o.orderStatus = :status) " +
           "AND (:branchId IS NULL OR sr.id.branchId = :branchId) " +
           "ORDER BY o.orderTime DESC")
    List<Order> findAllByStatusAndBranch(@Param("status") EOrderStatus status, @Param("branchId") Integer branchId);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.customer c " +
           "LEFT JOIN FETCH o.tickets t " +
           "LEFT JOIN FETCH t.showtime st " +
           "LEFT JOIN FETCH st.movie m " +
           "LEFT JOIN FETCH st.screenRoom sr " +
           "WHERE (:branchId IS NULL OR sr.id.branchId = :branchId) " +
           "ORDER BY o.orderTime DESC")
    List<Order> findRecentOrders(@Param("branchId") Integer branchId, org.springframework.data.domain.Pageable pageable);
}
