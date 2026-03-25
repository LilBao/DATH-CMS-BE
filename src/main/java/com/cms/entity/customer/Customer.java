package com.cms.entity.customer;

import com.cms.common.enums.AuthProviderType;
import com.cms.common.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Maps to: Customer.CUSTOMER table
 */
@Entity
@Table(name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_customer_phone", columnNames = "phone_number")
        })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "cUserId")
public class Customer {

    @Id
    @Column(name = "c_user_id", length = 20, nullable = false)
    private String cUserId;

    @Column(name = "c_name", length = 100, nullable = false)
    private String cName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", length = 1)
    private Sex sex;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    /**
     * BCrypt hashed password. Nullable for OAuth2 users.
     */
    @Column(name = "e_password", length = 255)
    private String ePassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", length = 15, nullable = false)
    @Builder.Default
    private UserType userType = UserType.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 20)
    @Builder.Default
    private AuthProviderType authProvider = AuthProviderType.LOCAL;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Membership membership;

    public enum Sex {
        M, F
    }
}
