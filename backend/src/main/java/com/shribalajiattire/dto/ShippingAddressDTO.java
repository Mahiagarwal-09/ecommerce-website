package com.shribalajiattire.dto;

import com.shribalajiattire.model.ShippingAddress;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDTO {
    @NotBlank
    private String fullName;
    
    @NotBlank
    private String addressLine1;
    
    private String addressLine2;
    
    @NotBlank
    private String city;
    
    @NotBlank
    private String state;
    
    @NotBlank
    private String postalCode;
    
    @NotBlank
    private String country;
    
    @NotBlank
    private String phone;
    
    public ShippingAddress toShippingAddress() {
        return ShippingAddress.builder()
                .fullName(fullName)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country(country)
                .phone(phone)
                .build();
    }
}
