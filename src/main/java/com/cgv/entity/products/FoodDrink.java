package com.cgv.entity.products;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: Products.FOODDRINK
 */
@Entity
@Table(name = "food_drink")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FoodDrink extends AddonItem {

    @Column(name = "p_type", length = 50, nullable = false)
    private String pType;

    @Column(name = "p_name", length = 255)
    private String pName;

    @Column(name = "quantity")
    private Integer quantity;
}
