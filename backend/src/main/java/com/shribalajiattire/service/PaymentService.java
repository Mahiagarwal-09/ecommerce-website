package com.shribalajiattire.service;

import com.shribalajiattire.model.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {
    
    @Value("${app.stripe.api-key}")
    private String stripeApiKey;
    
    public String createPaymentIntent(Order order) {
        try {
            Stripe.apiKey = stripeApiKey;
            
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(order.getTotalCents())
                    .setCurrency(order.getCurrency().toLowerCase())
                    .setDescription("Order #" + order.getId())
                    .putMetadata("orderId", order.getId().toString())
                    .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            log.info("Created payment intent: {}", paymentIntent.getId());
            
            return paymentIntent.getId();
        } catch (StripeException e) {
            log.error("Stripe payment failed", e);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }
    
    public String getPaymentClientSecret(String paymentIntentId) {
        try {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            log.error("Failed to retrieve payment intent", e);
            throw new RuntimeException("Failed to retrieve payment details");
        }
    }
}
