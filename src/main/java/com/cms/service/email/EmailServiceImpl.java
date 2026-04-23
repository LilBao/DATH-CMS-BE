package com.cms.service.email;

import com.cms.entity.booking.Order;
import com.cms.entity.screening.Ticket;
import com.cms.entity.cinema.SeatId;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@cms.com}")
    private String fromEmail;

    @Override
    @Async
    public void sendEmail(String[] to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}", (Object) to, e);
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public void sendOrderConfirmationEmail(String to, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Đặt vé thành công - CMS Cinema");

            Context context = new Context();
            context.setVariable("order", order);
            
            if (order.getTickets() != null && !order.getTickets().isEmpty()) {
                Ticket firstTicket = order.getTickets().get(0);
                context.setVariable("movieName", firstTicket.getShowtime().getMovie().getMName());
                context.setVariable("branchName", firstTicket.getSeat().getScreenRoom().getBranch().getBName());
                context.setVariable("roomName", firstTicket.getSeat().getScreenRoom().getId());
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                context.setVariable("showTime", firstTicket.getShowtime().getStartTime().format(formatter));
                context.setVariable("posterUrl", firstTicket.getShowtime().getMovie().getPosterUrl() != null 
                    ? firstTicket.getShowtime().getMovie().getPosterUrl() 
                    : "https://via.placeholder.com/300x450");
                
                String seatsInfo = order.getTickets().stream()
                        .map(t -> {
                            SeatId sId = t.getSeat().getId();
                            return (char)(sId.getSRow() + 'A' - 1) + String.valueOf(sId.getSColumn());
                        })
                        .collect(Collectors.joining(", "));
                context.setVariable("seatsInfo", seatsInfo);
                
                // Using an external QR Code API for the email if the QR code is just a string payload,
                // or if it's already an image URL, just use it. Let's assume we use api.qrserver.com to generate image dynamically.
                String qrData = firstTicket.getQrCode() != null ? firstTicket.getQrCode() : "ORDER-" + order.getOrderId();
                context.setVariable("qrCodeUrl", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + qrData);
            }

            String html = templateEngine.process("ticket-email", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Ticket confirmation email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send ticket email to {}", to, e);
        }
    }
}
