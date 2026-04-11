package com.cms.repository.booking;

import com.cms.entity.booking.Order;
import com.cms.enums.EOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomerCUserIdOrderByOrderTimeDesc(String cUserId);

    List<Order> findByOrderStatus(EOrderStatus status);

    List<Order> findByCustomerEmail(String email);
}
