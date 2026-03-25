package com.cms.entity.products;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps to: Products.MERCHANDISE
 */
@Entity
@Table(name = "merchandise")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Merchandise extends AddonItem {

    @Column(name = "avail_num")
    private Integer availNum;

    @Column(name = "merch_name", length = 255)
    private String merchName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
