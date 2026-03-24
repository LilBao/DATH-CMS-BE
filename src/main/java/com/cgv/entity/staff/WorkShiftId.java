package com.cgv.entity.staff;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Composite PK: (StartTime, EndTime, WDate)
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkShiftId implements Serializable {

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * WDate: 1 = Monday ... 7 = Sunday
     */
    @Column(name = "w_date", nullable = false)
    private Integer wDate;
}
