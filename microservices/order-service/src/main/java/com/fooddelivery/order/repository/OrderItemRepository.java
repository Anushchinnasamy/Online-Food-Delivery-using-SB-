package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity operations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find order items by order ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Find order items by menu item ID (for analytics)
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    List<OrderItem> findByMenuItemId(@Param("menuItemId") Long menuItemId);

    /**
     * Count order items by menu item ID
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    long countByMenuItemId(@Param("menuItemId") Long menuItemId);

    /**
     * Find popular menu items by restaurant (top ordered items)
     */
    @Query("SELECT oi.menuItemId, oi.itemName, COUNT(oi) as orderCount " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.restaurantId = :restaurantId " +
           "GROUP BY oi.menuItemId, oi.itemName " +
           "ORDER BY orderCount DESC")
    List<Object[]> findPopularMenuItemsByRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Calculate total quantity sold for a menu item
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    Long getTotalQuantitySoldForMenuItem(@Param("menuItemId") Long menuItemId);

    /**
     * Find order items with special instructions
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.specialInstructions IS NOT NULL AND oi.specialInstructions != ''")
    List<OrderItem> findOrderItemsWithSpecialInstructions();
}