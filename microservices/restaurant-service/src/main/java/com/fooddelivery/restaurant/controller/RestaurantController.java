package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.request.RestaurantCreateRequestDTO;
import com.fooddelivery.restaurant.dto.request.RestaurantUpdateRequestDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantDetailResponseDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantResponseDTO;
import com.fooddelivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for restaurant management operations
 */
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    // ==================== RESTAURANT ADMIN ENDPOINTS ====================

    /**
     * Create a new restaurant (Restaurant Admin)
     */
    @PostMapping
    public ResponseEntity<RestaurantDetailResponseDTO> createRestaurant(
            @Valid @RequestBody RestaurantCreateRequestDTO request) {
        log.info("Creating restaurant: {}", request.getName());
        
        RestaurantDetailResponseDTO response = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update restaurant details (Restaurant Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDetailResponseDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantUpdateRequestDTO request) {
        log.info("Updating restaurant with ID: {}", id);
        
        RestaurantDetailResponseDTO response = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update restaurant status - open/close (Restaurant Admin)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantDetailResponseDTO> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        log.info("Updating restaurant status for ID: {}", id);
        
        Boolean isOpen = request.get("is_open");
        if (isOpen == null) {
            return ResponseEntity.badRequest().build();
        }
        
        RestaurantDetailResponseDTO response = restaurantService.updateRestaurantStatus(id, isOpen);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete restaurant (Restaurant Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRestaurant(@PathVariable Long id) {
        log.info("Deleting restaurant with ID: {}", id);
        
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(Map.of("message", "Restaurant deleted successfully"));
    }

    // ==================== PLATFORM ADMIN ENDPOINTS ====================

    /**
     * Approve restaurant (Platform Admin)
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<RestaurantDetailResponseDTO> approveRestaurant(@PathVariable Long id) {
        log.info("Approving restaurant with ID: {}", id);
        
        RestaurantDetailResponseDTO response = restaurantService.approveRestaurant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Suspend restaurant (Platform Admin)
     */
    @PutMapping("/{id}/suspend")
    public ResponseEntity<RestaurantDetailResponseDTO> suspendRestaurant(@PathVariable Long id) {
        log.info("Suspending restaurant with ID: {}", id);
        
        RestaurantDetailResponseDTO response = restaurantService.suspendRestaurant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all restaurants for admin (Platform Admin)
     */
    @GetMapping("/admin")
    public ResponseEntity<Page<RestaurantDetailResponseDTO>> getAllRestaurantsForAdmin(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching all restaurants for admin");
        
        Page<RestaurantDetailResponseDTO> response = restaurantService.getAllRestaurantsForAdmin(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurants by approval status (Platform Admin)
     */
    @GetMapping("/admin/approval-status")
    public ResponseEntity<Page<RestaurantDetailResponseDTO>> getRestaurantsByApprovalStatus(
            @RequestParam Boolean approved,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching restaurants by approval status: {}", approved);
        
        Page<RestaurantDetailResponseDTO> response = restaurantService.getRestaurantsByApprovalStatus(approved, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurant details by ID (Admin view)
     */
    @GetMapping("/{id}/admin")
    public ResponseEntity<RestaurantDetailResponseDTO> getRestaurantDetailsById(@PathVariable Long id) {
        log.debug("Fetching restaurant details for ID: {}", id);
        
        RestaurantDetailResponseDTO response = restaurantService.getRestaurantDetailsById(id);
        return ResponseEntity.ok(response);
    }

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Get all restaurants (Customer)
     */
    @GetMapping
    public ResponseEntity<Page<RestaurantResponseDTO>> getAllRestaurants(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching all restaurants for customers");
        
        Page<RestaurantResponseDTO> response = restaurantService.getAllRestaurants(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurant by ID (Customer)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long id) {
        log.debug("Fetching restaurant with ID: {}", id);
        
        RestaurantResponseDTO response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Search restaurants (Customer)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantResponseDTO>> searchRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "10.0") Double radius,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Searching restaurants with filters - city: {}, cuisine: {}, name: {}", city, cuisine, name);
        
        Page<RestaurantResponseDTO> response;
        
        // Search by location if coordinates provided
        if (latitude != null && longitude != null) {
            response = restaurantService.getRestaurantsWithinRadius(latitude, longitude, radius, pageable);
        }
        // Search by name if provided
        else if (name != null && !name.trim().isEmpty()) {
            response = restaurantService.searchRestaurantsByName(name.trim(), pageable);
        }
        // Search by city and/or cuisine
        else {
            response = restaurantService.searchRestaurants(city, cuisine, pageable);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurants by city (Customer)
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByCity(
            @PathVariable String city,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching restaurants by city: {}", city);
        
        Page<RestaurantResponseDTO> response = restaurantService.getRestaurantsByCity(city, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get restaurants by cuisine (Customer)
     */
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByCuisine(
            @PathVariable String cuisine,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching restaurants by cuisine: {}", cuisine);
        
        Page<RestaurantResponseDTO> response = restaurantService.getRestaurantsByCuisine(cuisine, pageable);
        return ResponseEntity.ok(response);
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Get available cities
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAvailableCities() {
        log.debug("Fetching available cities");
        
        List<String> cities = restaurantService.getAvailableCities();
        return ResponseEntity.ok(cities);
    }

    /**
     * Get available cuisine types
     */
    @GetMapping("/cuisines")
    public ResponseEntity<List<String>> getAvailableCuisineTypes() {
        log.debug("Fetching available cuisine types");
        
        List<String> cuisines = restaurantService.getAvailableCuisineTypes();
        return ResponseEntity.ok(cuisines);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "restaurant-service",
            "timestamp", System.currentTimeMillis()
        ));
    }

    // ==================== INTERNAL ENDPOINTS ====================

    /**
     * Update restaurant rating (Internal - called by review service)
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<Map<String, String>> updateRestaurantRating(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        log.info("Updating rating for restaurant ID: {}", id);
        
        BigDecimal rating = new BigDecimal(request.get("rating").toString());
        Integer totalReviews = Integer.valueOf(request.get("total_reviews").toString());
        
        restaurantService.updateRestaurantRating(id, rating, totalReviews);
        return ResponseEntity.ok(Map.of("message", "Rating updated successfully"));
    }
}