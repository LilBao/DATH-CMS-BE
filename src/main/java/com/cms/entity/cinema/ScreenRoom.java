package com.cms.entity.cinema;

import com.cms.enums.ERType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps to: Cinema.SCREENROOM
 */
@Entity
@Table(name = "screen_room")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ScreenRoom {

    @EmbeddedId
    private ScreenRoomId id;

    @Column(name = "r_type", length = 30)
    private ERType rType;

    @Column(name = "r_capacity")
    private Integer rCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("branchId")
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "screenRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Seat> seats = new ArrayList<>();
}
