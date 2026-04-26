package com.fooddelivery.delivery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for Order Service client
 */
@Component
@Slf4j
public class OrderServiceClientFallback implements OrderServiceClient {

    @Override
    public void updateOrderDeliveryStatus(Long orderId, String deliveryStatus) {
        log.warn("Order service unavailable - using fallback for delivery status update: orderId={}, status={}", 
            orderId, deliveryStatus);
        // In fallback, we log the update but can't actually update the order
        // You might want to implement a retry mechanism or queue the update
    }

    @Override
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        log.warn("Order service unavailable - using fallback for order details: orderId={}", orderId);
        // Return a default/empty response
        return new OrderDetailsDTO(orderId, "UNKNOWN", null, null, "UNKNOWN", "UNKNOWN", null, null, null);
    }

    @Override
    public void validateOrderForDelivery(Long orderId) {
        log.warn("Order service unavailable - using fallback for order validation: orderId={}", orderId);
        // In fallback, we assume validation passes
        // In production, you might want to implement more sophisticated fallback logic
    }
}