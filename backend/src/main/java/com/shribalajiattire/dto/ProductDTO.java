package com.shribalajiattire.dto;

import com.shribalajiattire.model.Product;
import com.shribalajiattire.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private Long priceCents;
    private Double price;
    private String currency;
    private List<String> sizes;
    private List<String> colors;
    private List<String> images;
    private Integer stock;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductDTO fromProduct(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .priceCents(product.getPriceCents())
                .price(product.getPriceCents() / 100.0)
                .currency(product.getCurrency())
                .sizes(product.getSizes())
                .colors(product.getColors())
                .images(product.getImages().stream()
                        .sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder()))
                        .map(ProductImage::getUrl)
                        .collect(Collectors.toList()))
                .stock(product.getStock())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
