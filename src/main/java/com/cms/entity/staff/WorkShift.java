package com.cms.entity.staff;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Staff.WORKSHIFT
 * Composite PK: (StartTime, EndTime, WDate)
 */
@Entity
@Table(name = "work_shift")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WorkShift {

    @EmbeddedId
    private WorkShiftId id;

    @Column(name = "work", length = 500, nullable = false)
    private String work;
}
