package com.cms.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Integer ticketId;
    private String movieName;
    private String screenRoomName;
    private String branchName;
    private String seatName;
    private LocalDateTime showtime;
    private BigDecimal price;
}