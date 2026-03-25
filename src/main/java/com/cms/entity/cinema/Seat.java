package com.cms.entity.cinema;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Cinema.SEAT
 * SType: 0 = Standard, 1 = VIP, 2 = COUPLE
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
    private Integer sType;

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
