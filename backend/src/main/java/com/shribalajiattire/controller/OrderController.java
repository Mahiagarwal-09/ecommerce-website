package com.shribalajiattire.controller;

import com.shribalajiattire.dto.CheckoutRequest;
import com.shribalajiattire.dto.OrderDTO;
import com.shribalajiattire.security.UserPrincipal;
import com.shribalajiattire.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CheckoutRequest request) {
        OrderDTO order = orderService.createOrder(userPrincipal.getId(), request);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> orders = orderService.getUserOrders(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(userPrincipal.getId(), id);
        return ResponseEntity.ok(order);
    }
}
