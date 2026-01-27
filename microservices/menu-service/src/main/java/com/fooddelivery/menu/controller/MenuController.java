package com.fooddelivery.menu.controller;

import com.fooddelivery.menu.dto.request.MenuItemCreateRequestDTO;
import com.fooddelivery.menu.dto.request.MenuItemUpdateRequestDTO;
import com.fooddelivery.menu.dto.response.MenuItemDetailResponseDTO;
import com.fooddelivery.menu.dto.response.MenuItemResponseDTO;
import com.fooddelivery.menu.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for menu management operations
 */
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuItemService menuItemService;

    // ==================== RESTAURANT ADMIN ENDPOINTS ====================

    /**
     * Create a new menu item (Restaurant Admin)
     */
    @PostMapping
    public ResponseEntity<MenuItemDetailResponseDTO> createMenuItem(
            @Valid @RequestBody MenuItemCreateRequestDTO request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {
        log.info("Creating menu item: {} for restaurant: {}", request.getName(), request.getRestaurantId());
        
        MenuItemDetailResponseDTO response = menuItemService.createMenuItem(request, restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update menu item details (Restaurant Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDetailResponseDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemUpdateRequestDTO request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {
        log.info("Updating menu item with ID: {}", id);
        
        MenuItemDetailResponseDTO response = menuItemService.updateMenuItem(id, request, restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update menu item availability (Restaurant Admin)
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<MenuItemDetailResponseDTO> updateMenuItemAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {
        log.info("Updating menu item availability for ID: {}", id);
        
        Boolean isAvailable = request.get("is_available");
        if (isAvailable == null) {
            return ResponseEntity.badRequest().build();
        }
        
        MenuItemDetailResponseDTO response = menuItemService.updateMenuItemAvailability(id, isAvailable, restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete menu item (Restaurant Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMenuItem(
            @PathVariable Long id,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {
        log.info("Deleting menu item with ID: {}", id);
        
        menuItemService.deleteMenuItem(id, restaurantId);
        return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully"));
    }

    /**
     * Get menu item by ID (Restaurant Admin)
     */
    @GetMapping("/{id}/admin")
    public ResponseEntity<MenuItemDetailResponseDTO> getMenuItemById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long restaurantId) {
        log.debug("Fetching menu item details for ID: {}", id);
        
        MenuItemDetailResponseDTO response = menuItemService.getMenuItemById(id, restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all menu items for restaurant (Restaurant Admin)
     */
    @GetMapping("/restaurant/{restaurantId}/admin")
    public ResponseEntity<List<MenuItemDetailResponseDTO>> getAllMenuItemsByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.debug("Fetching all menu items for restaurant: {}", restaurantId);
        
        List<MenuItemDetailResponseDTO> response = menuItemService.getAllMenuItemsByRestaurantId(restaurantId, currentUserRestaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Bulk update restaurant menu availability (Restaurant Admin)
     */
    @PutMapping("/restaurant/{restaurantId}/availability")
    public ResponseEntity<Map<String, String>> updateRestaurantMenuAvailability(
            @PathVariable Long restaurantId,
            @RequestBody Map<String, Boolean> request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.info("Bulk updating menu availability for restaurant: {}", restaurantId);
        
        Boolean isAvailable = request.get("is_available");
        if (isAvailable == null) {
            return ResponseEntity.badRequest().build();
        }
        
        menuItemService.updateRestaurantMenuAvailability(restaurantId, isAvailable, currentUserRestaurantId);
        return ResponseEntity.ok(Map.of("message", "Restaurant menu availability updated successfully"));
    }

    /**
     * Bulk update category menu availability (Restaurant Admin)
     */
    @PutMapping("/restaurant/{restaurantId}/category/{category}/availability")
    public ResponseEntity<Map<String, String>> updateCategoryMenuAvailability(
            @PathVariable Long restaurantId,
            @PathVariable String category,
            @RequestBody Map<String, Boolean> request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.info("Bulk updating menu availability for restaurant: {} category: {}", restaurantId, category);
        
        Boolean isAvailable = request.get("is_available");
        if (isAvailable == null) {
            return ResponseEntity.badRequest().build();
        }
        
        menuItemService.updateCategoryMenuAvailability(restaurantId, category, isAvailable, currentUserRestaurantId);
        return ResponseEntity.ok(Map.of("message", "Category menu availability updated successfully"));
    }

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Get menu items by restaurant ID (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
        log.debug("Fetching menu items for restaurant: {}", restaurantId);
        
        List<MenuItemResponseDTO> response = menuItemService.getMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get menu items by restaurant ID with pagination (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/page")
    public ResponseEntity<Page<MenuItemResponseDTO>> getMenuItemsByRestaurantIdWithPagination(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching menu items for restaurant: {} with pagination", restaurantId);
        
        Page<MenuItemResponseDTO> response = menuItemService.getMenuItemsByRestaurantId(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get menu items by restaurant and category (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByRestaurantIdAndCategory(
            @PathVariable Long restaurantId,
            @PathVariable String category) {
        log.debug("Fetching menu items for restaurant: {} and category: {}", restaurantId, category);
        
        List<MenuItemResponseDTO> response = menuItemService.getMenuItemsByRestaurantIdAndCategory(restaurantId, category);
        return ResponseEntity.ok(response);
    }

    /**
     * Get vegetarian menu items by restaurant (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/vegetarian")
    public ResponseEntity<List<MenuItemResponseDTO>> getVegetarianMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
        log.debug("Fetching vegetarian menu items for restaurant: {}", restaurantId);
        
        List<MenuItemResponseDTO> response = menuItemService.getVegetarianMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get non-vegetarian menu items by restaurant (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/non-vegetarian")
    public ResponseEntity<List<MenuItemResponseDTO>> getNonVegetarianMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
        log.debug("Fetching non-vegetarian menu items for restaurant: {}", restaurantId);
        
        List<MenuItemResponseDTO> response = menuItemService.getNonVegetarianMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search menu items by name within a restaurant (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/search")
    public ResponseEntity<List<MenuItemResponseDTO>> searchMenuItemsByName(
            @PathVariable Long restaurantId,
            @RequestParam String name) {
        log.debug("Searching menu items by name: {} for restaurant: {}", name, restaurantId);
        
        List<MenuItemResponseDTO> response = menuItemService.searchMenuItemsByName(restaurantId, name);
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories for a restaurant (Customer)
     */
    @GetMapping("/restaurant/{restaurantId}/categories")
    public ResponseEntity<List<String>> getCategoriesByRestaurantId(@PathVariable Long restaurantId) {
        log.debug("Fetching categories for restaurant: {}", restaurantId);
        
        List<String> categories = menuItemService.getCategoriesByRestaurantId(restaurantId);
        return ResponseEntity.ok(categories);
    }

    // ==================== INTERNAL SERVICE ENDPOINTS ====================

    /**
     * Get menu items by IDs (Internal - for order validation)
     */
    @PostMapping("/batch")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByIds(@RequestBody Map<String, List<Long>> request) {
        List<Long> menuItemIds = request.get("menu_item_ids");
        if (menuItemIds == null || menuItemIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.debug("Fetching menu items by IDs: {}", menuItemIds);
        
        List<MenuItemResponseDTO> response = menuItemService.getMenuItemsByIds(menuItemIds);
        return ResponseEntity.ok(response);
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "menu-service",
            "timestamp", System.currentTimeMillis()
        ));
    }
}