package com.shribalajiattire.service;

import com.shribalajiattire.dto.CartItemDTO;
import com.shribalajiattire.dto.CheckoutRequest;
import com.shribalajiattire.dto.OrderDTO;
import com.shribalajiattire.model.Order;
import com.shribalajiattire.model.OrderItem;
import com.shribalajiattire.model.Product;
import com.shribalajiattire.model.User;
import com.shribalajiattire.repository.OrderRepository;
import com.shribalajiattire.repository.ProductRepository;
import com.shribalajiattire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    
    @Transactional
    public OrderDTO createOrder(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShipping().toShippingAddress())
                .status(Order.OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .currency("INR")
                .build();
        
        long totalCents = 0;
        
        for (CartItemDTO cartItem : request.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPriceCents(product.getPriceCents())
                    .size(cartItem.getSize())
                    .color(cartItem.getColor())
                    .build();
            
            order.addItem(orderItem);
            totalCents += product.getPriceCents() * cartItem.getQuantity();
            
            // Update stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalCents(totalCents);
        order = orderRepository.save(order);
        
        // Process payment
        if ("stripe".equals(request.getPaymentMethod())) {
            String paymentId = paymentService.createPaymentIntent(order);
            order.setPaymentId(paymentId);
            order = orderRepository.save(order);
        } else {
            // Mock payment - auto-approve
            order.setStatus(Order.OrderStatus.PAID);
            order.setPaymentId("MOCK_" + System.currentTimeMillis());
            order = orderRepository.save(order);
        }
        
        return OrderDTO.fromOrder(order);
    }
    
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(OrderDTO::fromOrder);
    }
    
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return OrderDTO.fromOrder(order);
    }
    
    public Page<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).map(OrderDTO::fromOrder);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(Order.OrderStatus.valueOf(status));
        order = orderRepository.save(order);
        
        return OrderDTO.fromOrder(order);
    }
    
    public OrderAnalytics getAnalytics() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        Long revenue = orderRepository.calculateRevenue(thirtyDaysAgo);
        Long orderCount = orderRepository.countOrdersSince(thirtyDaysAgo);
        
        return new OrderAnalytics(
                revenue != null ? revenue : 0L,
                orderCount != null ? orderCount : 0L
        );
    }
    
    public record OrderAnalytics(Long revenueCents, Long orderCount) {
        public Double revenue() {
            return revenueCents / 100.0;
        }
    }
}
