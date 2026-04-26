package com.cms.entity.staff;

import com.cms.common.enums.UserType;
import com.cms.entity.cinema.Branch;
import com.cms.enums.ESex;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps to: Staff.EMPLOYEE
 * EUserID format: EMP001, EMP002, ...
 */
@Entity
@Table(name = "employee",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employee_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_employee_phone", columnNames = "phone_number")
        })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "eUserId")
public class Employee {

    @Id
    @Column(name = "e_user_id", length = 20, nullable = false)
    private String eUserId;

    @Column(name = "e_name", length = 100, nullable = false)
    private String eName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", length = 1)
    private ESex sex;

    @Column(name = "birthday")
    private java.time.LocalDate birthday;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "e_password", length = 255, nullable = false)
    private String ePassword;

    @Column(name = "salary", precision = 10, scale = 2, nullable = false)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", length = 15, nullable = false)
    private UserType userType;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    /**
     * Self-referencing: Manager → nhân viên
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manage_id")
    private Employee manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Employee> subordinates = new HashSet<>();

    /**
     * Chi nhánh làm việc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /**
     * Ca làm việc
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "work",
            joinColumns = @JoinColumn(name = "e_user_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "start_time"),
                    @JoinColumn(name = "end_time"),
                    @JoinColumn(name = "w_date")
            }
    )
    @Builder.Default
    @ToString.Exclude
    private Set<WorkShift> workShifts = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
