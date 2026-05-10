package com.cms.entity.booking;

import com.cms.entity.customer.Customer;
import com.cms.entity.products.AddonItem;
import com.cms.entity.screening.Ticket;
import com.cms.entity.staff.Employee;
import com.cms.enums.EOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps to: Booking.ORDERS
 */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "orderId")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @CreatedDate
    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;
    
    @Column(name = "original_total", precision = 10, scale = 2)
    private BigDecimal originalTotal;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20)
    @Builder.Default
    private EOrderStatus orderStatus = EOrderStatus.PENDING;

    /**
     * Khách hàng đặt vé online
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user_id", nullable = true)
    private Customer customer;

    /**
     * Nhân viên bán vé tại quầy (nullable nếu đặt online)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_user_id")
    private Employee employee;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<AddonItem> addonItems = new ArrayList<>();
}
