package com.cms.entity.customer;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Customer.MEMBERSHIP
 * Mỗi Customer có tối đa 1 Membership
 */
@Entity
@Table(name = "membership")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "memberId")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "point", nullable = false)
    @Builder.Default
    private Integer point = 0;

    /**
     * MemberRank: 1 = Bronze, 2 = Silver, 3 = Gold, 4 = Diamond
     */
    @Column(name = "member_rank", nullable = false)
    @Builder.Default
    private Integer memberRank = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user_id", nullable = false, unique = true)
    private Customer customer;
}
