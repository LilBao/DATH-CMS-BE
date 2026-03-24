package com.cgv.entity.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite PK: (BranchID, RoomID, SRow, SColumn)
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SeatId implements Serializable {

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "s_row")
    private Integer sRow;

    @Column(name = "s_column")
    private Integer sColumn;
}
