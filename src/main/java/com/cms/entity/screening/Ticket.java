package com.cms.entity.screening;

import com.cms.entity.booking.Order;
import com.cms.entity.cinema.Seat;
import com.cms.enums.ETicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Maps to: Screening.TICKETS
 */
@Entity
@Table(name = "ticket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "ticketId")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "day_sold")
    private LocalDate daySold;

    @Column(name = "t_price", precision = 10, scale = 2)
    private BigDecimal tPrice;

    /**
     * Mã QR vé
     */
    @Column(name = "qr_code", length = 500)
    private String qrCode;

    /**
     * Trạng thái vé: SOLD, CHECKED_IN, REFUNDED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", length = 20)
    @Builder.Default
    private ETicketStatus ticketStatus = ETicketStatus.SOLD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Ghế được đặt
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "branch_id"),
            @JoinColumn(name = "room_id"),
            @JoinColumn(name = "s_row"),
            @JoinColumn(name = "s_column")
    })
    private Seat seat;

}
