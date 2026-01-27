package com.fooddelivery.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for Order Service communication
 */
@FeignClient(
    name = "order-service",
    fallback = OrderServiceClientFallback.class
)
public interface OrderServiceClient {

    /**
     * Update order delivery status
     */
    @PutMapping("/orders/{orderId}/delivery-status")
    void updateOrderDeliveryStatus(
        @PathVariable("orderId") Long orderId,
        @RequestParam("deliveryStatus") String deliveryStatus
    );

    /**
     * Get order details
     */
    @GetMapping("/orders/{orderId}")
    OrderDetailsDTO getOrderDetails(@PathVariable("orderId") Long orderId);

    /**
     * Validate order for delivery assignment
     */
    @GetMapping("/orders/{orderId}/validate-delivery")
    void validateOrderForDelivery(@PathVariable("orderId") Long orderId);

    /**
     * DTO for order details
     */
    record OrderDetailsDTO(
        Long id,
        String orderNumber,
        Long userId,
        Long restaurantId,
        String orderStatus,
        String paymentStatus,
        String deliveryAddress,
        Double deliveryLatitude,
        Double deliveryLongitude
    ) {}
}