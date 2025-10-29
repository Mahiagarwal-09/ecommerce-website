package com.shribalajiattire.dto;

import com.shribalajiattire.model.Order;
import com.shribalajiattire.model.OrderItem;
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
public class OrderDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private List<OrderItemDTO> items;
    private Long totalCents;
    private Double total;
    private String currency;
    private ShippingAddressDTO shippingAddress;
    private String status;
    private String paymentId;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrderDTO fromOrder(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(order.getItems().stream()
                        .map(OrderItemDTO::fromOrderItem)
                        .collect(Collectors.toList()))
                .totalCents(order.getTotalCents())
                .total(order.getTotalCents() / 100.0)
                .currency(order.getCurrency())
                .shippingAddress(ShippingAddressDTO.builder()
                        .fullName(order.getShippingAddress().getFullName())
                        .addressLine1(order.getShippingAddress().getAddressLine1())
                        .addressLine2(order.getShippingAddress().getAddressLine2())
                        .city(order.getShippingAddress().getCity())
                        .state(order.getShippingAddress().getState())
                        .postalCode(order.getShippingAddress().getPostalCode())
                        .country(order.getShippingAddress().getCountry())
                        .phone(order.getShippingAddress().getPhone())
                        .build())
                .status(order.getStatus().name())
                .paymentId(order.getPaymentId())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private Long unitPriceCents;
        private Double unitPrice;
        private String size;
        private String color;
        
        public static OrderItemDTO fromOrderItem(OrderItem item) {
            return OrderItemDTO.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPriceCents(item.getUnitPriceCents())
                    .unitPrice(item.getUnitPriceCents() / 100.0)
                    .size(item.getSize())
                    .color(item.getColor())
                    .build();
        }
    }
}
