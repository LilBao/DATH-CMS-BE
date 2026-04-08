package com.cms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDrinkRequest {

    @NotBlank(message = "Product type is required")
    @Size(max = 50)
    private String pType;

    @NotBlank(message = "Product name is required")
    @Size(max = 255)
    private String pName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Min(value = 0)
    private Integer quantity;
}
