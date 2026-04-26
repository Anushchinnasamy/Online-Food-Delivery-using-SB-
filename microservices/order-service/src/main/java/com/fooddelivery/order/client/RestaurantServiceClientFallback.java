package com.fooddelivery.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback implementation for Restaurant Service client
 */
@Component
@Slf4j
public class RestaurantServiceClientFallback implements RestaurantServiceClient {

    @Override
    public ResponseEntity<Map<String, Object>> getRestaurantDetails(Long restaurantId) {
        log.warn("Restaurant service is unavailable, falling back for restaurant ID: {}", restaurantId);
        // Return null to indicate service unavailable - validation will be skipped
        return ResponseEntity.ok(null);
    }
}