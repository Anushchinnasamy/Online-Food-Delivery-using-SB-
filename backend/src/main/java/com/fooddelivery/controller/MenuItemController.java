package com.fooddelivery.controller;

import com.fooddelivery.dto.MenuItemDTO;
import com.fooddelivery.security.CustomUserDetails;
import com.fooddelivery.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for menu item operations
 */
@RestController
@RequestMapping("/menu-items")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Menu Items", description = "Menu item management and browsing APIs")
public class MenuItemController {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemController.class);

    @Autowired
    private MenuItemService menuItemService;

    /**
     * Get menu item by ID (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        logger.info("GET /menu-items/{} - Fetching menu item", id);
        MenuItemDTO menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    /**
     * Get menu items by restaurant (Public)
     */
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get restaurant menu", description = "Retrieve all available menu items for a restaurant (Public)")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable Long restaurantId) {
        logger.info("GET /menu-items/restaurant/{} - Fetching menu items", restaurantId);
        List<MenuItemDTO> menuItems = menuItemService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Search menu items (Public)
     */
    @GetMapping("/search")
    @Operation(summary = "Search menu items", description = "Search menu items by name, category, or dietary preferences (Public)")
    public ResponseEntity<List<MenuItemDTO>> searchMenuItems(
            @Parameter(description = "Item name") @RequestParam(required = false) String name,
            @Parameter(description = "Restaurant ID") @RequestParam(required = false) Long restaurantId,
            @Parameter(description = "Category") @RequestParam(required = false) String category,
            @Parameter(description = "Vegetarian only") @RequestParam(required = false) Boolean vegetarian) {
        logger.info("GET /menu-items/search - name: {}, restaurantId: {}, category: {}, vegetarian: {}", 
                    name, restaurantId, category, vegetarian);

        List<MenuItemDTO> menuItems;
        if (name != null && !name.isEmpty()) {
            menuItems = menuItemService.searchByName(name);
        } else if (restaurantId != null && category != null) {
            menuItems = menuItemService.getByCategory(restaurantId, category);
        } else if (restaurantId != null && Boolean.TRUE.equals(vegetarian)) {
            menuItems = menuItemService.getVegetarianItems(restaurantId);
        } else if (restaurantId != null) {
            menuItems = menuItemService.getAvailableMenuItems(restaurantId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(menuItems);
    }

    /**
     * Create menu item (Restaurant Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    @Operation(summary = "Create menu item", description = "Add a new item to restaurant menu (Restaurant Admin only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @Valid @RequestBody MenuItemDTO menuItemDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("POST /menu-items - Creating menu item: {}", menuItemDTO.getName());
        MenuItemDTO created = menuItemService.createMenuItem(menuItemDTO, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update menu item (Restaurant Admin only - owner)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemDTO menuItemDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("PUT /menu-items/{} - Updating menu item", id);
        MenuItemDTO updated = menuItemService.updateMenuItem(id, menuItemDTO, userDetails.getId());
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete menu item (Restaurant Admin only - owner)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<?> deleteMenuItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("DELETE /menu-items/{} - Deleting menu item", id);
        menuItemService.deleteMenuItem(id, userDetails.getId());
        return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully"));
    }

    /**
     * Toggle menu item availability (Restaurant Admin only - owner)
     */
    @PutMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<MenuItemDTO> toggleAvailability(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("PUT /menu-items/{}/toggle-availability - Toggling availability", id);
        MenuItemDTO updated = menuItemService.toggleAvailability(id, userDetails.getId());
        return ResponseEntity.ok(updated);
    }
}
