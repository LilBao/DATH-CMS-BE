package com.cms.entity.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite PK: (BranchID, RoomID)
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ScreenRoomId implements Serializable {

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "room_id")
    private Integer roomId;
}
