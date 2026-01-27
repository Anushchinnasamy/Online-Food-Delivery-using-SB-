package com.fooddelivery.repository;

import com.fooddelivery.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for MenuItem entity operations
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find menu items by restaurant ID
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.deletedAt IS NULL ORDER BY m.category, m.name")
    List<MenuItem> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find available menu items by restaurant ID
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category, m.name")
    List<MenuItem> findAvailableByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find menu items by restaurant ID and category
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category = :category AND m.deletedAt IS NULL ORDER BY m.name")
    List<MenuItem> findByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);

    /**
     * Find available menu items by restaurant ID and category
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category = :category AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.name")
    List<MenuItem> findAvailableByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);

    /**
     * Find menu items by name containing (case-insensitive)
     */
    @Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.isAvailable = true AND m.deletedAt IS NULL")
    List<MenuItem> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find vegetarian menu items by restaurant
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isVegetarian = true AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category, m.name")
    List<MenuItem> findVegetarianByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find vegan menu items by restaurant
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isVegan = true AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category, m.name")
    List<MenuItem> findVeganByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find menu items by price range
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.price BETWEEN :minPrice AND :maxPrice AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.price")
    List<MenuItem> findByRestaurantIdAndPriceRange(@Param("restaurantId") Long restaurantId, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find distinct categories by restaurant
     */
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category IS NOT NULL AND m.deletedAt IS NULL ORDER BY m.category")
    List<String> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find popular menu items (most ordered)
     */
    @Query("SELECT m FROM MenuItem m JOIN m.orderItems oi WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL " +
           "GROUP BY m ORDER BY COUNT(oi) DESC")
    List<MenuItem> findPopularByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find menu items with quick preparation time
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.preparationTimeMinutes <= :maxPrepTime AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.preparationTimeMinutes")
    List<MenuItem> findQuickPrepByRestaurantId(@Param("restaurantId") Long restaurantId, @Param("maxPrepTime") Integer maxPrepTime);

    /**
     * Search menu items across all restaurants
     */
    @Query("SELECT m FROM MenuItem m JOIN m.restaurant r WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "m.isAvailable = true AND m.deletedAt IS NULL AND " +
           "r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<MenuItem> searchMenuItems(@Param("searchTerm") String searchTerm);

    /**
     * Search menu items in a specific city
     */
    @Query("SELECT m FROM MenuItem m JOIN m.restaurant r WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "r.city = :city AND " +
           "m.isAvailable = true AND m.deletedAt IS NULL AND " +
           "r.isActive = true AND r.isApproved = true AND r.deletedAt IS NULL")
    List<MenuItem> searchMenuItemsByCity(@Param("searchTerm") String searchTerm, @Param("city") String city);

    /**
     * Find menu items by multiple filters
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND " +
           "(:category IS NULL OR m.category = :category) AND " +
           "(:isVegetarian IS NULL OR m.isVegetarian = :isVegetarian) AND " +
           "(:isVegan IS NULL OR m.isVegan = :isVegan) AND " +
           "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
           "m.isAvailable = true AND m.deletedAt IS NULL " +
           "ORDER BY m.category, m.name")
    List<MenuItem> findByFilters(
            @Param("restaurantId") Long restaurantId,
            @Param("category") String category,
            @Param("isVegetarian") Boolean isVegetarian,
            @Param("isVegan") Boolean isVegan,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Count menu items by restaurant
     */
    long countByRestaurantIdAndDeletedAtIsNull(Long restaurantId);

    /**
     * Count available menu items by restaurant
     */
    long countByRestaurantIdAndIsAvailableTrueAndDeletedAtIsNull(Long restaurantId);

    /**
     * Find cheapest menu items by restaurant
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.price ASC")
    List<MenuItem> findCheapestByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find menu items by ingredients containing
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND " +
           "LOWER(m.ingredients) LIKE LOWER(CONCAT('%', :ingredient, '%')) AND " +
           "m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.name")
    List<MenuItem> findByRestaurantIdAndIngredientsContaining(@Param("restaurantId") Long restaurantId, @Param("ingredient") String ingredient);

    /**
     * Find low-calorie menu items
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND " +
           "m.caloriesPerServing IS NOT NULL AND m.caloriesPerServing <= :maxCalories AND " +
           "m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.caloriesPerServing")
    List<MenuItem> findLowCalorieByRestaurantId(@Param("restaurantId") Long restaurantId, @Param("maxCalories") Integer maxCalories);
}