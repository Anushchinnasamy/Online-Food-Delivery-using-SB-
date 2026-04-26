package com.fooddelivery.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Feign client for Menu Service communication
 */
@FeignClient(name = "menu-service", fallback = MenuServiceClientFallback.class)
public interface MenuServiceClient {

    /**
     * Get menu items by IDs for order validation
     */
    @PostMapping("/menus/batch")
    ResponseEntity<List<Map<String, Object>>> getMenuItemsByIds(@RequestBody Map<String, List<Long>> request);
}