package com.shribalajiattire.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    @NotEmpty
    private List<CartItemDTO> cartItems;
    
    @NotNull
    private ShippingAddressDTO shipping;
    
    private String paymentMethod = "stripe";
}
