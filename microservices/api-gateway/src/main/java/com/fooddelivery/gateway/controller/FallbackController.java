package com.fooddelivery.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller for circuit breaker functionality
 * Provides fallback responses when downstream services are unavailable
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Auth service fallback
     */
    @PostMapping("/auth")
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        return createFallbackResponse("Auth Service", 
            "Authentication service is temporarily unavailable. Please try again later.");
    }

    /**
     * Restaurant service fallback
     */
    @PostMapping("/restaurant")
    @GetMapping("/restaurant")
    public ResponseEntity<Map<String, Object>> restaurantServiceFallback() {
        return createFallbackResponse("Restaurant Service", 
            "Restaurant service is temporarily unavailable. Please try again later.");
    }

    /**
     * Menu service fallback
     */
    @PostMapping("/menu")
    @GetMapping("/menu")
    public ResponseEntity<Map<String, Object>> menuServiceFallback() {
        return createFallbackResponse("Menu Service", 
            "Menu service is temporarily unavailable. Please try again later.");
    }

    /**
     * Order service fallback
     */
    @PostMapping("/order")
    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        return createFallbackResponse("Order Service", 
            "Order service is temporarily unavailable. Please try again later.");
    }

    /**
     * Payment service fallback
     */
    @PostMapping("/payment")
    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        return createFallbackResponse("Payment Service", 
            "Payment service is temporarily unavailable. Please try again later.");
    }

    /**
     * Delivery service fallback
     */
    @PostMapping("/delivery")
    @GetMapping("/delivery")
    public ResponseEntity<Map<String, Object>> deliveryServiceFallback() {
        return createFallbackResponse("Delivery Service", 
            "Delivery service is temporarily unavailable. Please try again later.");
    }

    /**
     * Notification service fallback
     */
    @PostMapping("/notification")
    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        return createFallbackResponse("Notification Service", 
            "Notification service is temporarily unavailable. Please try again later.");
    }

    /**
     * Create standardized fallback response
     */
    private ResponseEntity<Map<String, Object>> createFallbackResponse(String serviceName, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("service", serviceName);
        response.put("message", message);
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("timestamp", LocalDateTime.now());
        response.put("fallback", true);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}