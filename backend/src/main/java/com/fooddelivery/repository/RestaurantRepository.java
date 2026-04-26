package com.fooddelivery.repository;

import com.fooddelivery.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Restaurant entity operations
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find active and approved restaurants
     */
    List<Restaurant> findByIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull();

    /**
     * Find active and approved restaurants with pagination
     */
    Page<Restaurant> findByIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(Pageable pageable);

    /**
     * Find restaurants by city (case-insensitive)
     */
    List<Restaurant> findByCityIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(String city);
    
    /**
     * Find restaurants by city
     */
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<Restaurant> findByCity(@Param("city") String city);

    /**
     * Find restaurants by city with pagination
     */
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    Page<Restaurant> findByCity(@Param("city") String city, Pageable pageable);

    /**
     * Find restaurants by cuisine type (case-insensitive)
     */
    List<Restaurant> findByCuisineTypeIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(String cuisineType);
    
    /**
     * Find restaurants by cuisine type
     */
    @Query("SELECT r FROM Restaurant r WHERE r.cuisineType = :cuisineType AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<Restaurant> findByCuisineType(@Param("cuisineType") String cuisineType);

    /**
     * Find restaurants by name containing (case-insensitive)
     */
    List<Restaurant> findByNameContainingIgnoreCaseAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(String name);
    
    /**
     * Find restaurants by name containing (case-insensitive) - alternative
     */
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<Restaurant> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find restaurants by minimum rating
     */
    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :minRating AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC")
    List<Restaurant> findByMinimumRating(@Param("minRating") BigDecimal minRating);

    /**
     * Find restaurants currently open
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isOpen = true AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL " +
           "AND CURRENT_TIME BETWEEN r.openingTime AND r.closingTime")
    List<Restaurant> findCurrentlyOpenRestaurants();

    /**
     * Find restaurants by owner
     */
    Optional<Restaurant> findByOwnerIdAndDeletedAtIsNull(Long ownerId);
    
    /**
     * Find restaurants by owner - alternative
     */
    @Query("SELECT r FROM Restaurant r WHERE r.owner.id = :ownerId AND r.deletedAt IS NULL")
    Optional<Restaurant> findByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Find restaurants pending approval
     */
    List<Restaurant> findByIsApprovedFalseAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find restaurants within delivery radius (simplified calculation)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.isOpen = true AND r.deletedAt IS NULL " +
           "AND r.latitude IS NOT NULL AND r.longitude IS NOT NULL " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(r.latitude)))) <= :radiusKm")
    List<Restaurant> findRestaurantsWithinRadius(@Param("lat") double latitude, @Param("lng") double longitude, @Param("radiusKm") double radiusKm);

    /**
     * Find top-rated restaurants
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL " +
           "AND r.totalReviews > 0 ORDER BY r.rating DESC, r.totalReviews DESC")
    List<Restaurant> findTopRatedRestaurants(Pageable pageable);

    /**
     * Find restaurants by cuisine type and city
     */
    @Query("SELECT r FROM Restaurant r WHERE r.cuisineType = :cuisineType AND r.city = :city " +
           "AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<Restaurant> findByCuisineTypeAndCity(@Param("cuisineType") String cuisineType, @Param("city") String city);

    /**
     * Find restaurants with fast delivery
     */
    @Query("SELECT r FROM Restaurant r WHERE r.deliveryTimeMinutes <= :maxDeliveryTime " +
           "AND r.isActive = true AND r.isApproved = true AND r.isOpen = true AND r.deletedAt IS NULL " +
           "ORDER BY r.deliveryTimeMinutes ASC")
    List<Restaurant> findFastDeliveryRestaurants(@Param("maxDeliveryTime") Integer maxDeliveryTime);

    /**
     * Search restaurants by multiple criteria
     */
    @Query("SELECT r FROM Restaurant r WHERE " +
           "(:city IS NULL OR r.city = :city) AND " +
           "(:cuisineType IS NULL OR r.cuisineType = :cuisineType) AND " +
           "(:minRating IS NULL OR r.rating >= :minRating) AND " +
           "(:maxDeliveryTime IS NULL OR r.deliveryTimeMinutes <= :maxDeliveryTime) AND " +
           "r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    Page<Restaurant> searchRestaurants(
            @Param("city") String city,
            @Param("cuisineType") String cuisineType,
            @Param("minRating") BigDecimal minRating,
            @Param("maxDeliveryTime") Integer maxDeliveryTime,
            Pageable pageable);

    /**
     * Count restaurants by city
     */
    long countByCityAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(String city);

    /**
     * Count restaurants by cuisine type
     */
    long countByCuisineTypeAndIsActiveTrueAndIsApprovedTrueAndDeletedAtIsNull(String cuisineType);

    /**
     * Find distinct cities with restaurants
     */
    @Query("SELECT DISTINCT r.city FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.city")
    List<String> findDistinctCities();

    /**
     * Find distinct cuisine types
     */
    @Query("SELECT DISTINCT r.cuisineType FROM Restaurant r WHERE r.cuisineType IS NOT NULL AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.cuisineType")
    List<String> findDistinctCuisineTypes();

    /**
     * Find restaurants with most orders (popular restaurants)
     */
    @Query("SELECT r FROM Restaurant r JOIN r.orders o WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL " +
           "GROUP BY r ORDER BY COUNT(o) DESC")
    List<Restaurant> findPopularRestaurants(Pageable pageable);
}