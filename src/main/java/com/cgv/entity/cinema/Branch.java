package com.cgv.entity.cinema;

import com.cgv.entity.staff.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to: Cinema.BRANCH
 */
@Entity
@Table(name = "branch")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "branchId")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "b_name", length = 100)
    private String bName;

    @Column(name = "b_address", length = 200)
    private String bAddress;

    /**
     * Quản lý chi nhánh
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manage_id")
    private Employee manager;

    /**
     * Số điện thoại chi nhánh (multi-value)
     */
    @ElementCollection
    @CollectionTable(name = "branch_phone_number",
            joinColumns = @JoinColumn(name = "branch_id"))
    @Column(name = "b_phone_number", length = 15)
    @Builder.Default
    private List<String> phoneNumbers = new ArrayList<>();

    /**
     * Phòng chiếu thuộc chi nhánh
     */
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ScreenRoom> screenRooms = new ArrayList<>();
}
