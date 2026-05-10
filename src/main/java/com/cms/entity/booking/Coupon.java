package com.cms.entity.booking;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps to: Booking.COUPON
 */
@Entity
@Table(name = "coupon")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "couponId")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Phần trăm giảm giá: 1-100
     */
    @Column(name = "sale_off", nullable = false)
    private Integer saleOff;

    @Column(name = "release_num")
    private Integer releaseNum;

    @Column(name = "avail_num")
    private Integer availNum;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
