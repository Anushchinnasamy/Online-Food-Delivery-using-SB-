package com.fooddelivery.repository;

import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
     * Find orders by customer ID
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find orders by customer ID with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * Find orders by restaurant ID
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders by restaurant ID with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.createdAt DESC")
    Page<Order> findByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    /**
     * Find orders by status
     */
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    /**
     * Find orders by restaurant ID and status
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantIdAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);

    /**
     * Find active orders by restaurant (orders that need attention)
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status IN ('PLACED', 'ACCEPTED', 'PREPARING', 'READY_FOR_PICKUP') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders by customer ID and status
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status);

    /**
     * Find orders created between dates
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders by restaurant between dates
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByRestaurantIdBetweenDates(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders ready for pickup
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'READY_FOR_PICKUP' ORDER BY o.updatedAt ASC")
    List<Order> findOrdersReadyForPickup();

    /**
     * Find orders ready for pickup in a city
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'READY_FOR_PICKUP' AND o.restaurant.city = :city ORDER BY o.updatedAt ASC")
    List<Order> findOrdersReadyForPickupInCity(@Param("city") String city);

    /**
     * Calculate total revenue for restaurant between dates
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRestaurantRevenue(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total platform revenue between dates
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculatePlatformRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders by restaurant and status
     */
    long countByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    /**
     * Count orders by customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Find recent orders (last N hours)
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("since") LocalDateTime since);

    /**
     * Find orders with high value (above threshold)
     */
    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minAmount ORDER BY o.totalAmount DESC")
    List<Order> findHighValueOrders(@Param("minAmount") BigDecimal minAmount);

    /**
     * Find orders by customer in date range
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByCustomerIdBetweenDates(@Param("customerId") Long customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders that can be cancelled (by customer)
     */
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status IN ('PLACED', 'ACCEPTED') ORDER BY o.createdAt DESC")
    List<Order> findCancellableOrdersByCustomer(@Param("customerId") Long customerId);

    /**
     * Find orders that can be cancelled (by restaurant)
     */
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status IN ('PLACED', 'ACCEPTED', 'PREPARING') ORDER BY o.createdAt DESC")
    List<Order> findCancellableOrdersByRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders with delivery delays (estimated time passed)
     */
    @Query("SELECT o FROM Order o WHERE o.estimatedDeliveryTime < CURRENT_TIMESTAMP AND o.status IN ('PICKED_UP', 'OUT_FOR_DELIVERY') ORDER BY o.estimatedDeliveryTime ASC")
    List<Order> findDelayedOrders();

    /**
     * Find top customers by order value
     */
    @Query("SELECT o.customer FROM Order o WHERE o.status = 'DELIVERED' GROUP BY o.customer ORDER BY SUM(o.totalAmount) DESC")
    List<Object> findTopCustomersByValue(Pageable pageable);

    /**
     * Find average order value for restaurant
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = 'DELIVERED'")
    BigDecimal findAverageOrderValueByRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Find orders by multiple statuses
     */
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByStatusIn(@Param("statuses") List<OrderStatus> statuses);

    /**
     * Find orders requiring delivery assignment
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'READY_FOR_PICKUP' AND o.delivery IS NULL ORDER BY o.updatedAt ASC")
    List<Order> findOrdersNeedingDeliveryAssignment();
}