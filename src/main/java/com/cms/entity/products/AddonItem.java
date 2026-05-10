package com.cms.entity.products;

import com.cms.entity.booking.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Maps to: Products.ADDONITEM
 * Base class cho FoodDrink và Merchandise (JOINED inheritance)
 */
@Entity
@Table(name = "addon_item")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "productId")
public class AddonItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "item_type", length = 50)
    private String itemType;

    @Column(name = "img_url", length = 1000)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
