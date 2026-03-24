package com.cgv.entity.cinema;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Cinema.SEAT
 * SType: false = Standard, true = VIP (cột 8-10)
 * SStatus: false = unavailable, true = available
 */
@Entity
@Table(name = "seat")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Seat {

    @EmbeddedId
    private SeatId id;

    /**
     * false = Standard, true = VIP
     */
    @Column(name = "s_type", nullable = false)
    private Boolean sType;

    /**
     * true = ghế đang hoạt động, false = không sử dụng
     */
    @Column(name = "s_status", nullable = false)
    @Builder.Default
    private Boolean sStatus = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(name = "room_id", referencedColumnName = "room_id", insertable = false, updatable = false)
    })
    private ScreenRoom screenRoom;
}
