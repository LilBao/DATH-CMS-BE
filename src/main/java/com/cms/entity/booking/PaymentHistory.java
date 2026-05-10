package com.cms.entity.booking;

import com.cms.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "historyId")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50, nullable = false)
    private EPaymentStatus paymentStatus;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * Mã code trả về từ cổng thanh toán (VD: vnp_ResponseCode, resultCode của MoMo)
     */
    @Column(name = "response_code", length = 50)
    private String responseCode;

    /**
     * Thông điệp từ cổng thanh toán hoặc hệ thống ghi nhận
     */
    @Column(name = "response_message", length = 255)
    private String responseMessage;

    /**
     * Payload thô (raw JSON hoặc query string) từ webhook/cổng thanh toán để đối soát
     */
    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
