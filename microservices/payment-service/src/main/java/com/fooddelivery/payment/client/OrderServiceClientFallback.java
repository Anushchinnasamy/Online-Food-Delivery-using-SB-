package com.fooddelivery.payment.client;

import com.fooddelivery.payment.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for Order Service client
 */
@Component
@Slf4j
public class OrderServiceClientFallback implements OrderServiceClient {

    @Override
    public void validateOrderForPayment(Long orderId, Long userId) {
        log.warn("Order service unavailable - using fallback for order validation: orderId={}, userId={}", orderId, userId);
        // In fallback, we assume validation passes
        // In production, you might want to implement more sophisticated fallback logic
    }

    @Override
    public void updateOrderPaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        log.warn("Order service unavailable - using fallback for payment status update: orderId={}, status={}", orderId, paymentStatus);
        // In fallback, we log the update but can't actually update the order
        // You might want to implement a retry mechanism or queue the update
    }

    @Override
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        log.warn("Order service unavailable - using fallback for order details: orderId={}", orderId);
        // Return a default/empty response
        return new OrderDetailsDTO(orderId, "UNKNOWN", null, null, "UNKNOWN", "UNKNOWN");
    }
}