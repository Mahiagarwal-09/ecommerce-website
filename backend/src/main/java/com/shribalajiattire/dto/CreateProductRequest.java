package com.shribalajiattire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String sku;
    
    private String description;
    
    @NotNull
    private Double price;
    
    private List<String> sizes;
    private List<String> colors;
    private Integer stock;
}
