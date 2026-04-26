package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.enums.OrderStatus;
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
 * Repository interface for Order entity operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by user ID
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") Long userId);

    /**
     * Find orders by user ID with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find orders by restaurant ID
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders by restaurant ID with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId ORDER BY o.createdAt DESC")
    Page<Order> findByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find orders by delivery partner ID
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryPartnerId = :deliveryPartnerId ORDER BY o.createdAt DESC")
    List<Order> findByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find orders by delivery partner ID with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryPartnerId = :deliveryPartnerId ORDER BY o.createdAt DESC")
    Page<Order> findByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId, Pageable pageable);

    /**
     * Find orders by user ID and status
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.orderStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndOrderStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    /**
     * Find orders by restaurant ID and status
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.orderStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantIdAndOrderStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    /**
     * Find orders by restaurant ID and status with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.orderStatus = :status ORDER BY o.createdAt DESC")
    Page<Order> findByRestaurantIdAndOrderStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status, Pageable pageable);

    /**
     * Find orders by status
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.createdAt DESC")
    List<Order> findByOrderStatus(@Param("status") OrderStatus status);

    /**
     * Find orders by multiple statuses
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByOrderStatusIn(@Param("statuses") List<OrderStatus> statuses);

    /**
     * Find orders created between dates
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders by user ID created between dates
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders by restaurant ID created between dates
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantIdAndCreatedAtBetween(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find active orders for restaurant (not delivered or cancelled)
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.orderStatus NOT IN ('DELIVERED', 'CANCELLED') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders ready for pickup (for delivery partner assignment)
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'READY_FOR_PICKUP' AND o.deliveryPartnerId IS NULL ORDER BY o.createdAt ASC")
    List<Order> findOrdersReadyForPickup();

    /**
     * Find orders ready for pickup in specific area (simplified by restaurant)
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'READY_FOR_PICKUP' AND o.deliveryPartnerId IS NULL AND o.restaurantId = :restaurantId ORDER BY o.createdAt ASC")
    List<Order> findOrdersReadyForPickupByRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Count orders by user ID
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Count orders by restaurant ID
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurantId = :restaurantId")
    long countByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Count orders by status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") OrderStatus status);

    /**
     * Count orders by restaurant ID and status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurantId = :restaurantId AND o.orderStatus = :status")
    long countByRestaurantIdAndOrderStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    /**
     * Update order status
     */
    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :status, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Assign delivery partner to order
     */
    @Modifying
    @Query("UPDATE Order o SET o.deliveryPartnerId = :deliveryPartnerId, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void assignDeliveryPartner(@Param("orderId") Long orderId, @Param("deliveryPartnerId") Long deliveryPartnerId, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Update estimated delivery time
     */
    @Modifying
    @Query("UPDATE Order o SET o.estimatedDeliveryTime = :estimatedTime, o.updatedAt = :updatedAt WHERE o.id = :orderId")
    void updateEstimatedDeliveryTime(@Param("orderId") Long orderId, @Param("estimatedTime") LocalDateTime estimatedTime, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Find recent orders by user (last 30 days)
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt >= :thirtyDaysAgo ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    /**
     * Find orders that need status update (stuck in processing states)
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('PLACED', 'ACCEPTED') AND o.createdAt < :cutoffTime")
    List<Order> findStaleOrders(@Param("cutoffTime") LocalDateTime cutoffTime);
}