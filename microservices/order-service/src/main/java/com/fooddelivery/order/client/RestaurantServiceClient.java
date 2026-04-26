package com.fooddelivery.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign client for Restaurant Service communication
 */
@FeignClient(name = "restaurant-service", fallback = RestaurantServiceClientFallback.class)
public interface RestaurantServiceClient {

    /**
     * Validate if restaurant exists and is active
     */
    @GetMapping("/restaurants/{id}")
    ResponseEntity<Map<String, Object>> getRestaurantDetails(@PathVariable("id") Long restaurantId);
}