package com.cms.repository.booking;

import com.cms.entity.booking.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder_OrderId(Integer orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    @Query("SELECT p FROM Payment p WHERE p.order.customer.cUserId = :cUserId")
    List<Payment> findByOrderCustomerCUserId(@Param("cUserId") String cUserId);
}
