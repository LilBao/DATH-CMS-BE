package com.cms.repository.booking;

import com.cms.entity.booking.Order;
import com.cms.enums.EOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
