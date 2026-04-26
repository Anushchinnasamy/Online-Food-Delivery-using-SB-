package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Restaurant entity operations
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurant by ID excluding soft deleted
     */
    @Query("SELECT r FROM Restaurant r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Restaurant> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Find restaurant by email excluding soft deleted
     */
    @Query("SELECT r FROM Restaurant r WHERE r.email = :email AND r.deletedAt IS NULL")
    Optional<Restaurant> findByEmailAndNotDeleted(@Param("email") String email);

    /**
     * Check if email exists (excluding soft deleted)
     */
    @Query("SELECT COUNT(r) > 0 FROM Restaurant r WHERE r.email = :email AND r.deletedAt IS NULL")
    boolean existsByEmailAndNotDeleted(@Param("email") String email);

    /**
     * Find all active and approved restaurants for customers
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> findVisibleRestaurants(Pageable pageable);

    /**
     * Find restaurants by city for customers
     */
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> findVisibleRestaurantsByCity(@Param("city") String city, Pageable pageable);

    /**
     * Find restaurants by cuisine type for customers
     */
    @Query("SELECT r FROM Restaurant r WHERE r.cuisineType = :cuisineType AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> findVisibleRestaurantsByCuisineType(@Param("cuisineType") String cuisineType, Pageable pageable);

    /**
     * Find restaurants by city and cuisine type for customers
     */
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.cuisineType = :cuisineType AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> findVisibleRestaurantsByCityAndCuisineType(@Param("city") String city, @Param("cuisineType") String cuisineType, Pageable pageable);

    /**
     * Search restaurants by name for customers
     */
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> searchVisibleRestaurantsByName(@Param("name") String name, Pageable pageable);

    /**
     * Find restaurants within radius (simplified distance calculation)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(r.latitude)))) <= :radiusKm " +
           "ORDER BY r.rating DESC, r.name ASC")
    Page<Restaurant> findVisibleRestaurantsWithinRadius(@Param("latitude") BigDecimal latitude, 
                                                       @Param("longitude") BigDecimal longitude, 
                                                       @Param("radiusKm") Double radiusKm, 
                                                       Pageable pageable);

    /**
     * Find all restaurants for admin (including inactive/unapproved)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Restaurant> findAllNotDeleted(Pageable pageable);

    /**
     * Find restaurants by approval status
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isApproved = :approved AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Restaurant> findByApprovalStatus(@Param("approved") Boolean approved, Pageable pageable);

    /**
     * Find restaurants by active status
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = :active AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Restaurant> findByActiveStatus(@Param("active") Boolean active, Pageable pageable);

    /**
     * Find currently open restaurants
     */
    @Query("SELECT r FROM Restaurant r WHERE r.isOpen = true AND r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.rating DESC")
    List<Restaurant> findCurrentlyOpenRestaurants();

    /**
     * Find restaurants by city (admin view)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Restaurant> findByCityNotDeleted(@Param("city") String city, Pageable pageable);

    /**
     * Find restaurants by cuisine type (admin view)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.cuisineType = :cuisineType AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Restaurant> findByCuisineTypeNotDeleted(@Param("cuisineType") String cuisineType, Pageable pageable);

    /**
     * Get distinct cities
     */
    @Query("SELECT DISTINCT r.city FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.city")
    List<String> findDistinctCities();

    /**
     * Get distinct cuisine types
     */
    @Query("SELECT DISTINCT r.cuisineType FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL ORDER BY r.cuisineType")
    List<String> findDistinctCuisineTypes();

    /**
     * Count restaurants by status
     */
    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    long countActiveApprovedRestaurants();

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isApproved = false AND r.deletedAt IS NULL")
    long countPendingApprovalRestaurants();

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isActive = false AND r.deletedAt IS NULL")
    long countInactiveRestaurants();

    /**
     * Soft delete restaurant
     */
    @Modifying
    @Query("UPDATE Restaurant r SET r.deletedAt = :deletedAt, r.isActive = false WHERE r.id = :id")
    void softDeleteRestaurant(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Update restaurant approval status
     */
    @Modifying
    @Query("UPDATE Restaurant r SET r.isApproved = :approved WHERE r.id = :id")
    void updateApprovalStatus(@Param("id") Long id, @Param("approved") Boolean approved);

    /**
     * Update restaurant active status
     */
    @Modifying
    @Query("UPDATE Restaurant r SET r.isActive = :active WHERE r.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

    /**
     * Update restaurant open status
     */
    @Modifying
    @Query("UPDATE Restaurant r SET r.isOpen = :open WHERE r.id = :id")
    void updateOpenStatus(@Param("id") Long id, @Param("open") Boolean open);

    /**
     * Update restaurant rating
     */
    @Modifying
    @Query("UPDATE Restaurant r SET r.rating = :rating, r.totalReviews = :totalReviews WHERE r.id = :id")
    void updateRating(@Param("id") Long id, @Param("rating") BigDecimal rating, @Param("totalReviews") Integer totalReviews);
}