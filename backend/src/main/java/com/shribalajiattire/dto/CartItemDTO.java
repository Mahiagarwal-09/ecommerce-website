package com.shribalajiattire.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemDTO {
    @NotNull
    private Long productId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    private String size;
    private String color;
}
