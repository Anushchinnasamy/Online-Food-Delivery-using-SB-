package com.fooddelivery.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback implementation for Menu Service client
 */
@Component
@Slf4j
public class MenuServiceClientFallback implements MenuServiceClient {

    @Override
    public ResponseEntity<List<Map<String, Object>>> getMenuItemsByIds(Map<String, List<Long>> request) {
        log.warn("Menu service is unavailable, falling back for menu items: {}", request.get("menu_item_ids"));
        // Return empty list to indicate service unavailable - order creation will fail gracefully
        return ResponseEntity.ok(Collections.emptyList());
    }
}