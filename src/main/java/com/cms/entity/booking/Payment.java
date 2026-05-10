package com.cms.entity.booking;

import com.cms.enums.EPaymentMethod;
import com.cms.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps to: Booking.PAYMENT
 */
@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "paymentId")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50, nullable = false)
    private EPaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50, nullable = false)
    @Builder.Default
    private EPaymentStatus paymentStatus = EPaymentStatus.PENDING;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @CreatedDate
    @Column(name = "payment_time", nullable = false, updatable = false)
    private LocalDateTime paymentTime;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<PaymentHistory> histories = new ArrayList<>();

}
