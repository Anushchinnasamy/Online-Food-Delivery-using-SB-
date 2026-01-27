package com.fooddelivery.payment.client;

import com.fooddelivery.payment.enums.PaymentStatus;
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
     * Validate order for payment
     */
    @GetMapping("/orders/{orderId}/validate-payment")
    void validateOrderForPayment(
        @PathVariable("orderId") Long orderId,
        @RequestParam("userId") Long userId
    );

    /**
     * Update order payment status
     */
    @PutMapping("/orders/{orderId}/payment-status")
    void updateOrderPaymentStatus(
        @PathVariable("orderId") Long orderId,
        @RequestParam("paymentStatus") PaymentStatus paymentStatus
    );

    /**
     * Get order details
     */
    @GetMapping("/orders/{orderId}")
    OrderDetailsDTO getOrderDetails(@PathVariable("orderId") Long orderId);

    /**
     * DTO for order details
     */
    record OrderDetailsDTO(
        Long id,
        String orderNumber,
        Long userId,
        Long restaurantId,
        String orderStatus,
        String paymentStatus
    ) {}
}