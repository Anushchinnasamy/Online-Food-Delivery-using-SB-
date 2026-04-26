package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.request.RestaurantCreateRequestDTO;
import com.fooddelivery.restaurant.dto.request.RestaurantUpdateRequestDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantDetailResponseDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantResponseDTO;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.exception.RestaurantNotFoundException;
import com.fooddelivery.restaurant.exception.RestaurantAlreadyExistsException;
import com.fooddelivery.restaurant.mapper.RestaurantMapper;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for restaurant management operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    /**
     * Create a new restaurant
     */
    public RestaurantDetailResponseDTO createRestaurant(RestaurantCreateRequestDTO request) {
        log.info("Creating new restaurant with email: {}", request.getEmail());

        // Check if restaurant with email already exists
        if (restaurantRepository.existsByEmailAndNotDeleted(request.getEmail())) {
            throw new RestaurantAlreadyExistsException("Restaurant with email " + request.getEmail() + " already exists");
        }

        // Validate operating hours
        validateOperatingHours(request.getOpeningTime(), request.getClosingTime());

        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant created successfully with ID: {}", restaurant.getId());
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Update restaurant details
     */
    public RestaurantDetailResponseDTO updateRestaurant(Long restaurantId, RestaurantUpdateRequestDTO request) {
        log.info("Updating restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);

        // Validate operating hours if provided
        if (request.getOpeningTime() != null && request.getClosingTime() != null) {
            validateOperatingHours(request.getOpeningTime(), request.getClosingTime());
        }

        restaurantMapper.updateEntityFromDTO(request, restaurant);
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully with ID: {}", restaurant.getId());
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Update restaurant status (open/close)
     */
    public RestaurantDetailResponseDTO updateRestaurantStatus(Long restaurantId, Boolean isOpen) {
        log.info("Updating restaurant status for ID: {} to {}", restaurantId, isOpen);

        Restaurant restaurant = findRestaurantById(restaurantId);
        
        if (!restaurant.isVisibleToCustomers()) {
            throw new IllegalStateException("Cannot open restaurant that is not active or approved");
        }

        restaurant.setIsOpen(isOpen);
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant status updated successfully for ID: {}", restaurant.getId());
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Approve restaurant (Platform Admin only)
     */
    public RestaurantDetailResponseDTO approveRestaurant(Long restaurantId) {
        log.info("Approving restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.approve();
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant approved successfully with ID: {}", restaurant.getId());
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Suspend restaurant (Platform Admin only)
     */
    public RestaurantDetailResponseDTO suspendRestaurant(Long restaurantId) {
        log.info("Suspending restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.suspend();
        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant suspended successfully with ID: {}", restaurant.getId());
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Soft delete restaurant
     */
    public void deleteRestaurant(Long restaurantId) {
        log.info("Deleting restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.softDelete();
        restaurantRepository.save(restaurant);

        log.info("Restaurant deleted successfully with ID: {}", restaurant.getId());
    }

    /**
     * Get restaurant by ID (customer view)
     */
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantById(Long restaurantId) {
        log.debug("Fetching restaurant with ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
            .filter(Restaurant::isVisibleToCustomers)
            .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));

        return restaurantMapper.toResponseDTO(restaurant);
    }

    /**
     * Get restaurant details by ID (admin view)
     */
    @Transactional(readOnly = true)
    public RestaurantDetailResponseDTO getRestaurantDetailsById(Long restaurantId) {
        log.debug("Fetching restaurant details with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);
        return restaurantMapper.toDetailResponseDTO(restaurant);
    }

    /**
     * Get all restaurants for customers
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> getAllRestaurants(Pageable pageable) {
        log.debug("Fetching all visible restaurants");

        Page<Restaurant> restaurants = restaurantRepository.findVisibleRestaurants(pageable);
        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Search restaurants by city
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> getRestaurantsByCity(String city, Pageable pageable) {
        log.debug("Fetching restaurants by city: {}", city);

        Page<Restaurant> restaurants = restaurantRepository.findVisibleRestaurantsByCity(city, pageable);
        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Search restaurants by cuisine type
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> getRestaurantsByCuisine(String cuisineType, Pageable pageable) {
        log.debug("Fetching restaurants by cuisine: {}", cuisineType);

        Page<Restaurant> restaurants = restaurantRepository.findVisibleRestaurantsByCuisineType(cuisineType, pageable);
        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Search restaurants by city and cuisine
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> searchRestaurants(String city, String cuisineType, Pageable pageable) {
        log.debug("Searching restaurants by city: {} and cuisine: {}", city, cuisineType);

        Page<Restaurant> restaurants;
        
        if (city != null && cuisineType != null) {
            restaurants = restaurantRepository.findVisibleRestaurantsByCityAndCuisineType(city, cuisineType, pageable);
        } else if (city != null) {
            restaurants = restaurantRepository.findVisibleRestaurantsByCity(city, pageable);
        } else if (cuisineType != null) {
            restaurants = restaurantRepository.findVisibleRestaurantsByCuisineType(cuisineType, pageable);
        } else {
            restaurants = restaurantRepository.findVisibleRestaurants(pageable);
        }

        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Search restaurants by name
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> searchRestaurantsByName(String name, Pageable pageable) {
        log.debug("Searching restaurants by name: {}", name);

        Page<Restaurant> restaurants = restaurantRepository.searchVisibleRestaurantsByName(name, pageable);
        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Get restaurants within radius
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> getRestaurantsWithinRadius(BigDecimal latitude, BigDecimal longitude, 
                                                                 Double radiusKm, Pageable pageable) {
        log.debug("Fetching restaurants within {}km of coordinates: {}, {}", radiusKm, latitude, longitude);

        Page<Restaurant> restaurants = restaurantRepository.findVisibleRestaurantsWithinRadius(
            latitude, longitude, radiusKm, pageable);
        return restaurants.map(restaurantMapper::toResponseDTO);
    }

    /**
     * Get all restaurants for admin
     */
    @Transactional(readOnly = true)
    public Page<RestaurantDetailResponseDTO> getAllRestaurantsForAdmin(Pageable pageable) {
        log.debug("Fetching all restaurants for admin");

        Page<Restaurant> restaurants = restaurantRepository.findAllNotDeleted(pageable);
        return restaurants.map(restaurantMapper::toDetailResponseDTO);
    }

    /**
     * Get restaurants by approval status
     */
    @Transactional(readOnly = true)
    public Page<RestaurantDetailResponseDTO> getRestaurantsByApprovalStatus(Boolean approved, Pageable pageable) {
        log.debug("Fetching restaurants by approval status: {}", approved);

        Page<Restaurant> restaurants = restaurantRepository.findByApprovalStatus(approved, pageable);
        return restaurants.map(restaurantMapper::toDetailResponseDTO);
    }

    /**
     * Get distinct cities
     */
    @Transactional(readOnly = true)
    public List<String> getAvailableCities() {
        log.debug("Fetching available cities");
        return restaurantRepository.findDistinctCities();
    }

    /**
     * Get distinct cuisine types
     */
    @Transactional(readOnly = true)
    public List<String> getAvailableCuisineTypes() {
        log.debug("Fetching available cuisine types");
        return restaurantRepository.findDistinctCuisineTypes();
    }

    /**
     * Update restaurant rating (called by review service)
     */
    public void updateRestaurantRating(Long restaurantId, BigDecimal newRating, Integer totalReviews) {
        log.info("Updating rating for restaurant ID: {} to {} with {} reviews", restaurantId, newRating, totalReviews);

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.updateRating(newRating, totalReviews);
        restaurantRepository.save(restaurant);

        log.info("Rating updated successfully for restaurant ID: {}", restaurantId);
    }

    /**
     * Helper method to find restaurant by ID
     */
    private Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findByIdAndNotDeleted(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    /**
     * Validate operating hours
     */
    private void validateOperatingHours(java.time.LocalTime openingTime, java.time.LocalTime closingTime) {
        if (openingTime.equals(closingTime)) {
            throw new IllegalArgumentException("Opening time and closing time cannot be the same");
        }
        // Note: We allow closing time to be before opening time for restaurants that operate past midnight
    }
}