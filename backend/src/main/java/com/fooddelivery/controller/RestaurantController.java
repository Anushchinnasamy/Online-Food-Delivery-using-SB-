package com.fooddelivery.controller;

import com.fooddelivery.dto.RestaurantDTO;
import com.fooddelivery.security.CustomUserDetails;
import com.fooddelivery.service.RestaurantService;
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
 * REST controller for restaurant operations
 */
@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Restaurants", description = "Restaurant management and browsing APIs")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Get all restaurants (Public)
     */
    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieve all active and approved restaurants (Public)")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        logger.info("GET /restaurants - Fetching all restaurants");
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Get restaurant by ID (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        logger.info("GET /restaurants/{} - Fetching restaurant", id);
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    /**
     * Search restaurants (Public)
     */
    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Search restaurants by city, cuisine, or name (Public)")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @Parameter(description = "City name") @RequestParam(required = false) String city,
            @Parameter(description = "Cuisine type") @RequestParam(required = false) String cuisine,
            @Parameter(description = "Restaurant name") @RequestParam(required = false) String name) {
        logger.info("GET /restaurants/search - city: {}, cuisine: {}, name: {}", city, cuisine, name);

        List<RestaurantDTO> restaurants;
        if (city != null && !city.isEmpty()) {
            restaurants = restaurantService.searchByCity(city);
        } else if (cuisine != null && !cuisine.isEmpty()) {
            restaurants = restaurantService.searchByCuisine(cuisine);
        } else if (name != null && !name.isEmpty()) {
            restaurants = restaurantService.searchByName(name);
        } else {
            restaurants = restaurantService.getAllRestaurants();
        }

        return ResponseEntity.ok(restaurants);
    }

    /**
     * Create restaurant (Restaurant Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    @Operation(summary = "Create restaurant", description = "Create a new restaurant (Restaurant Admin only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RestaurantDTO> createRestaurant(
            @Valid @RequestBody RestaurantDTO restaurantDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("POST /restaurants - Creating restaurant: {}", restaurantDTO.getName());
        RestaurantDTO created = restaurantService.createRestaurant(restaurantDTO, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update restaurant (Restaurant Admin only - owner)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantDTO restaurantDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("PUT /restaurants/{} - Updating restaurant", id);
        RestaurantDTO updated = restaurantService.updateRestaurant(id, restaurantDTO, userDetails.getId());
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete restaurant (Restaurant Admin only - owner)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<?> deleteRestaurant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("DELETE /restaurants/{} - Deleting restaurant", id);
        restaurantService.deleteRestaurant(id, userDetails.getId());
        return ResponseEntity.ok(Map.of("message", "Restaurant deleted successfully"));
    }

    /**
     * Approve restaurant (Platform Admin only)
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<RestaurantDTO> approveRestaurant(@PathVariable Long id) {
        logger.info("PUT /restaurants/{}/approve - Approving restaurant", id);
        RestaurantDTO approved = restaurantService.approveRestaurant(id);
        return ResponseEntity.ok(approved);
    }

    /**
     * Get my restaurant (Restaurant Admin only)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantDTO> getMyRestaurant(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("GET /restaurants/my - Fetching restaurant for owner: {}", userDetails.getId());
        RestaurantDTO restaurant = restaurantService.getRestaurantByOwnerId(userDetails.getId());
        return ResponseEntity.ok(restaurant);
    }
}
