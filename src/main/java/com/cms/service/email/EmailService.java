package com.cms.service.email;

import com.cms.entity.booking.Order;

public interface EmailService {
    void sendEmail(String[] to, String subject, String text);
    void sendOrderConfirmationEmail(String to, Integer orderId);
}
