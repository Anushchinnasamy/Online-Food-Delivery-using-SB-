package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Delivery entity
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Find delivery by order ID
     */
    Optional<Delivery> findByOrderId(Long orderId);

    /**
     * Find deliveries by delivery partner ID
     */
    List<Delivery> findByDeliveryPartnerIdOrderByCreatedAtDesc(Long deliveryPartnerId);

    /**
     * Find deliveries by delivery partner ID with pagination
     */
    Page<Delivery> findByDeliveryPartnerIdOrderByCreatedAtDesc(Long deliveryPartnerId, Pageable pageable);

    /**
     * Find deliveries by status
     */
    List<Delivery> findByDeliveryStatusOrderByCreatedAtDesc(DeliveryStatus deliveryStatus);

    /**
     * Find deliveries by delivery partner and status
     */
    List<Delivery> findByDeliveryPartnerIdAndDeliveryStatusOrderByCreatedAtDesc(
        Long deliveryPartnerId, DeliveryStatus deliveryStatus);

    /**
     * Find active delivery for delivery partner
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartnerId = :deliveryPartnerId " +
           "AND d.deliveryStatus NOT IN ('DELIVERED', 'CANCELLED') ORDER BY d.assignedAt DESC")
    Optional<Delivery> findActiveDeliveryByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Check if delivery partner has active delivery
     */
    @Query("SELECT COUNT(d) > 0 FROM Delivery d WHERE d.deliveryPartnerId = :deliveryPartnerId " +
           "AND d.deliveryStatus NOT IN ('DELIVERED', 'CANCELLED')")
    boolean hasActiveDelivery(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find deliveries assigned to delivery partner
     */
    List<Delivery> findByDeliveryPartnerIdAndDeliveryStatusInOrderByAssignedAtDesc(
        Long deliveryPartnerId, List<DeliveryStatus> statuses);

    /**
     * Find deliveries by date range
     */
    @Query("SELECT d FROM Delivery d WHERE d.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY d.createdAt DESC")
    List<Delivery> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find delayed deliveries
     */
    @Query("SELECT d FROM Delivery d WHERE d.estimatedDeliveryTime < :currentTime " +
           "AND d.deliveryStatus NOT IN ('DELIVERED', 'CANCELLED')")
    List<Delivery> findDelayedDeliveries(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find deliveries that need reassignment (rejected multiple times)
     */
    @Query("SELECT d FROM Delivery d WHERE d.rejectionCount >= :maxRejections " +
           "AND d.deliveryStatus = 'ASSIGNED'")
    List<Delivery> findDeliveriesNeedingReassignment(@Param("maxRejections") Integer maxRejections);

    /**
     * Count deliveries by status
     */
    long countByDeliveryStatus(DeliveryStatus deliveryStatus);

    /**
     * Count deliveries by delivery partner and status
     */
    long countByDeliveryPartnerIdAndDeliveryStatus(Long deliveryPartnerId, DeliveryStatus deliveryStatus);

    /**
     * Find deliveries by multiple statuses
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryStatus IN :statuses ORDER BY d.createdAt DESC")
    List<Delivery> findByDeliveryStatusIn(@Param("statuses") List<DeliveryStatus> statuses);

    /**
     * Find recent deliveries for delivery partner
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartnerId = :deliveryPartnerId " +
           "ORDER BY d.createdAt DESC")
    List<Delivery> findRecentDeliveriesByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId, 
                                                          Pageable pageable);

    /**
     * Find completed deliveries by delivery partner
     */
    List<Delivery> findByDeliveryPartnerIdAndDeliveryStatusOrderByDeliveredAtDesc(
        Long deliveryPartnerId, DeliveryStatus deliveryStatus);

    /**
     * Find deliveries with customer ratings
     */
    @Query("SELECT d FROM Delivery d WHERE d.customerRating IS NOT NULL " +
           "AND d.deliveryPartnerId = :deliveryPartnerId ORDER BY d.deliveredAt DESC")
    List<Delivery> findRatedDeliveriesByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Calculate average delivery time for delivery partner
     */
    @Query("SELECT AVG(d.actualDeliveryTimeMinutes) FROM Delivery d " +
           "WHERE d.deliveryPartnerId = :deliveryPartnerId AND d.deliveryStatus = 'DELIVERED' " +
           "AND d.actualDeliveryTimeMinutes IS NOT NULL")
    Double getAverageDeliveryTimeByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Calculate average rating for delivery partner
     */
    @Query("SELECT AVG(CAST(d.customerRating AS double)) FROM Delivery d " +
           "WHERE d.deliveryPartnerId = :deliveryPartnerId AND d.customerRating IS NOT NULL")
    Double getAverageRatingByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Check if order already has delivery assigned
     */
    boolean existsByOrderId(Long orderId);
}