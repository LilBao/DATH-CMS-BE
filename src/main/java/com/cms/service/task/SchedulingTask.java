package com.cms.service.task;

import com.cms.entity.booking.Order;
import com.cms.enums.EOrderStatus;
import com.cms.repository.booking.OrderRepository;
import com.cms.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingTask {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    /**
     * Tự động hủy các đơn PENDING quá 15 phút.
     * Chạy mỗi 5 phút.
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void autoCancelPendingOrders() {
        log.info("Running auto-cancel for pending orders...");
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(15);
        List<Order> pendingOrders = orderRepository.findPendingOrdersBefore(timeout);
        
        for (Order order : pendingOrders) {
            log.info("Cancelling order ID: {}", order.getOrderId());
            order.setOrderStatus(EOrderStatus.CANCELLED);
        }
        orderRepository.saveAll(pendingOrders);
    }

    /**
     * Gửi mail nhắc lịch trước 1 giờ chiếu.
     * Chạy mỗi 30 phút.
     */
    @Scheduled(fixedRate = 1800000)
    public void sendShowtimeReminders() {
        log.info("Running showtime reminders...");
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime upcoming = now.plusHours(1).plusMinutes(30); // Lấy khoảng 1h-1h30 tới để tránh bỏ sót

        List<Order> orders = orderRepository.findPaidOrdersWithUpcomingShowtime(today, now, upcoming);
        
        for (Order order : orders) {
            if (order.getCustomer() != null && order.getCustomer().getEmail() != null) {
                emailService.sendReminderEmail(order.getCustomer().getEmail(), order.getOrderId());
            }
        }
    }
}
