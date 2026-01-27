package com.fooddelivery.menu.repository;

import com.fooddelivery.menu.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MenuItem entity operations
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find menu item by ID excluding soft deleted
     */
    @Query("SELECT m FROM MenuItem m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<MenuItem> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Find all menu items for a restaurant (customer view - only available)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    List<MenuItem> findAvailableMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find all menu items for a restaurant with pagination (customer view - only available)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    Page<MenuItem> findAvailableMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find all menu items for a restaurant (admin view - including unavailable)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    List<MenuItem> findAllMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find all menu items for a restaurant with pagination (admin view - including unavailable)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    Page<MenuItem> findAllMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find menu items by restaurant and category (customer view)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.category = :category AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.name ASC")
    List<MenuItem> findAvailableMenuItemsByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);

    /**
     * Find menu items by restaurant and category (admin view)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.category = :category AND m.deletedAt IS NULL ORDER BY m.name ASC")
    List<MenuItem> findAllMenuItemsByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);

    /**
     * Find vegetarian menu items by restaurant (customer view)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isVegetarian = true AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    List<MenuItem> findAvailableVegetarianMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find non-vegetarian menu items by restaurant (customer view)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isVegetarian = false AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    List<MenuItem> findAvailableNonVegetarianMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find distinct categories for a restaurant
     */
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.category ASC")
    List<String> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find distinct categories for a restaurant (admin view)
     */
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deletedAt IS NULL ORDER BY m.category ASC")
    List<String> findAllDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Search menu items by name within a restaurant
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.isAvailable = true AND m.deletedAt IS NULL ORDER BY m.name ASC")
    List<MenuItem> searchAvailableMenuItemsByRestaurantIdAndName(@Param("restaurantId") Long restaurantId, @Param("name") String name);

    /**
     * Count menu items by restaurant
     */
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deletedAt IS NULL")
    long countMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Count available menu items by restaurant
     */
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isAvailable = true AND m.deletedAt IS NULL")
    long countAvailableMenuItemsByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Count menu items by restaurant and category
     */
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.category = :category AND m.deletedAt IS NULL")
    long countMenuItemsByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);

    /**
     * Check if menu item exists for restaurant (excluding soft deleted)
     */
    @Query("SELECT COUNT(m) > 0 FROM MenuItem m WHERE m.restaurantId = :restaurantId AND LOWER(m.name) = LOWER(:name) AND m.deletedAt IS NULL")
    boolean existsByRestaurantIdAndNameIgnoreCase(@Param("restaurantId") Long restaurantId, @Param("name") String name);

    /**
     * Check if menu item exists for restaurant excluding current item (for updates)
     */
    @Query("SELECT COUNT(m) > 0 FROM MenuItem m WHERE m.restaurantId = :restaurantId AND LOWER(m.name) = LOWER(:name) AND m.id != :id AND m.deletedAt IS NULL")
    boolean existsByRestaurantIdAndNameIgnoreCaseAndIdNot(@Param("restaurantId") Long restaurantId, @Param("name") String name, @Param("id") Long id);

    /**
     * Soft delete menu item
     */
    @Modifying
    @Query("UPDATE MenuItem m SET m.deletedAt = :deletedAt, m.isAvailable = false WHERE m.id = :id")
    void softDeleteMenuItem(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Update menu item availability
     */
    @Modifying
    @Query("UPDATE MenuItem m SET m.isAvailable = :available WHERE m.id = :id AND m.deletedAt IS NULL")
    void updateAvailability(@Param("id") Long id, @Param("available") Boolean available);

    /**
     * Bulk update availability for restaurant menu items
     */
    @Modifying
    @Query("UPDATE MenuItem m SET m.isAvailable = :available WHERE m.restaurantId = :restaurantId AND m.deletedAt IS NULL")
    void updateAvailabilityByRestaurantId(@Param("restaurantId") Long restaurantId, @Param("available") Boolean available);

    /**
     * Bulk update availability for menu items by category
     */
    @Modifying
    @Query("UPDATE MenuItem m SET m.isAvailable = :available WHERE m.restaurantId = :restaurantId AND m.category = :category AND m.deletedAt IS NULL")
    void updateAvailabilityByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category, @Param("available") Boolean available);

    /**
     * Find menu items by multiple IDs (for order validation)
     */
    @Query("SELECT m FROM MenuItem m WHERE m.id IN :ids AND m.isAvailable = true AND m.deletedAt IS NULL")
    List<MenuItem> findAvailableMenuItemsByIds(@Param("ids") List<Long> ids);

    /**
     * Find menu items by restaurant and availability status
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.isAvailable = :available AND m.deletedAt IS NULL ORDER BY m.category ASC, m.name ASC")
    List<MenuItem> findMenuItemsByRestaurantIdAndAvailability(@Param("restaurantId") Long restaurantId, @Param("available") Boolean available);
}