package com.fooddelivery.service;

import com.fooddelivery.dto.RestaurantDTO;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import com.fooddelivery.exception.BadRequestException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.exception.UnauthorizedException;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for restaurant operations
 */
@Service
@Transactional
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all active and approved restaurants
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getAllRestaurants() {
        logger.info("Fetching all active restaurants");
        return restaurantRepository.findByIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get restaurant by ID
     */
    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantById(Long id) {
        logger.info("Fetching restaurant with ID: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        return convertToDTO(restaurant);
    }

    /**
     * Search restaurants by city
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> searchByCity(String city) {
        logger.info("Searching restaurants in city: {}", city);
        return restaurantRepository.findByCityIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search restaurants by cuisine type
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> searchByCuisine(String cuisineType) {
        logger.info("Searching restaurants with cuisine: {}", cuisineType);
        return restaurantRepository.findByCuisineTypeIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(cuisineType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search restaurants by name
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> searchByName(String name) {
        logger.info("Searching restaurants with name containing: {}", name);
        return restaurantRepository.findByNameContainingIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new restaurant
     */
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO, Long ownerId) {
        logger.info("Creating new restaurant: {}", restaurantDTO.getName());

        User owner = userRepository.findById(ownerId)
                .filter(u -> u.getIsActive() && !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        if (owner.getRole() != UserRole.RESTAURANT_ADMIN) {
            throw new BadRequestException("User must have RESTAURANT_ADMIN role to create a restaurant");
        }

        // Check if owner already has a restaurant
        if (owner.getRestaurant() != null) {
            throw new BadRequestException("User already owns a restaurant");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setDescription(restaurantDTO.getDescription());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setCity(restaurantDTO.getCity());
        restaurant.setPincode(restaurantDTO.getPincode());
        restaurant.setLatitude(restaurantDTO.getLatitude());
        restaurant.setLongitude(restaurantDTO.getLongitude());
        restaurant.setPhone(restaurantDTO.getPhone());
        restaurant.setEmail(restaurantDTO.getEmail());
        restaurant.setCuisineType(restaurantDTO.getCuisineType());
        restaurant.setImageUrl(restaurantDTO.getImageUrl());
        restaurant.setDeliveryTimeMinutes(restaurantDTO.getDeliveryTimeMinutes());
        restaurant.setMinimumOrderAmount(restaurantDTO.getMinimumOrderAmount());
        restaurant.setDeliveryFee(restaurantDTO.getDeliveryFee());
        restaurant.setOpeningTime(restaurantDTO.getOpeningTime());
        restaurant.setClosingTime(restaurantDTO.getClosingTime());
        restaurant.setOwner(owner);
        restaurant.setIsActive(true);
        restaurant.setIsApproved(false); // Requires admin approval
        restaurant.setIsOpen(true);

        restaurant = restaurantRepository.save(restaurant);
        logger.info("Restaurant created successfully with ID: {}", restaurant.getId());

        return convertToDTO(restaurant);
    }

    /**
     * Update restaurant
     */
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO, Long userId) {
        logger.info("Updating restaurant with ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));

        // Check if user is the owner
        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this restaurant");
        }

        restaurant.setName(restaurantDTO.getName());
        restaurant.setDescription(restaurantDTO.getDescription());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setCity(restaurantDTO.getCity());
        restaurant.setPincode(restaurantDTO.getPincode());
        restaurant.setLatitude(restaurantDTO.getLatitude());
        restaurant.setLongitude(restaurantDTO.getLongitude());
        restaurant.setPhone(restaurantDTO.getPhone());
        restaurant.setEmail(restaurantDTO.getEmail());
        restaurant.setCuisineType(restaurantDTO.getCuisineType());
        restaurant.setImageUrl(restaurantDTO.getImageUrl());
        restaurant.setDeliveryTimeMinutes(restaurantDTO.getDeliveryTimeMinutes());
        restaurant.setMinimumOrderAmount(restaurantDTO.getMinimumOrderAmount());
        restaurant.setDeliveryFee(restaurantDTO.getDeliveryFee());
        restaurant.setOpeningTime(restaurantDTO.getOpeningTime());
        restaurant.setClosingTime(restaurantDTO.getClosingTime());
        restaurant.setIsOpen(restaurantDTO.getIsOpen());

        restaurant = restaurantRepository.save(restaurant);
        logger.info("Restaurant updated successfully");

        return convertToDTO(restaurant);
    }

    /**
     * Delete restaurant (soft delete)
     */
    public void deleteRestaurant(Long id, Long userId) {
        logger.info("Deleting restaurant with ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));

        // Check if user is the owner
        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this restaurant");
        }

        restaurant.softDelete();
        restaurantRepository.save(restaurant);
        logger.info("Restaurant deleted successfully");
    }

    /**
     * Approve restaurant (Admin only)
     */
    public RestaurantDTO approveRestaurant(Long id) {
        logger.info("Approving restaurant with ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));

        restaurant.setIsApproved(true);
        restaurant = restaurantRepository.save(restaurant);
        logger.info("Restaurant approved successfully");

        return convertToDTO(restaurant);
    }

    /**
     * Get restaurant by owner ID
     */
    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantByOwnerId(Long ownerId) {
        logger.info("Fetching restaurant for owner ID: {}", ownerId);
        Restaurant restaurant = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for this owner"));
        return convertToDTO(restaurant);
    }

    /**
     * Convert Restaurant entity to DTO
     */
    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        dto.setCity(restaurant.getCity());
        dto.setPincode(restaurant.getPincode());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setImageUrl(restaurant.getImageUrl());
        dto.setRating(restaurant.getRating());
        dto.setTotalReviews(restaurant.getTotalReviews());
        dto.setDeliveryTimeMinutes(restaurant.getDeliveryTimeMinutes());
        dto.setMinimumOrderAmount(restaurant.getMinimumOrderAmount());
        dto.setDeliveryFee(restaurant.getDeliveryFee());
        dto.setIsActive(restaurant.getIsActive());
        dto.setIsApproved(restaurant.getIsApproved());
        dto.setIsOpen(restaurant.getIsOpen());
        dto.setOpeningTime(restaurant.getOpeningTime());
        dto.setClosingTime(restaurant.getClosingTime());
        dto.setOwnerId(restaurant.getOwner().getId());
        dto.setOwnerName(restaurant.getOwner().getName());
        dto.setCreatedAt(restaurant.getCreatedAt());
        dto.setUpdatedAt(restaurant.getUpdatedAt());
        return dto;
    }
}
