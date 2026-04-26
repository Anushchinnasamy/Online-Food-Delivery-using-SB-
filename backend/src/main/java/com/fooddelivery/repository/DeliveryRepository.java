package com.fooddelivery.repository;

import com.fooddelivery.entity.Delivery;
import com.fooddelivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Delivery entity operations
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Find delivery by tracking number
     */
    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    /**
     * Find delivery by order ID
     */
    @Query("SELECT d FROM Delivery d WHERE d.order.id = :orderId")
    Optional<Delivery> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Find deliveries by delivery partner
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId ORDER BY d.createdAt DESC")
    List<Delivery> findByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find deliveries by status
     */
    List<Delivery> findByStatusOrderByCreatedAtDesc(DeliveryStatus status);

    /**
     * Find available deliveries (assigned but not accepted)
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'ASSIGNED' AND d.deliveryPartner IS NULL ORDER BY d.createdAt ASC")
    List<Delivery> findAvailableDeliveries();

    /**
     * Find available deliveries in city
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'ASSIGNED' AND d.deliveryPartner IS NULL " +
           "AND d.order.restaurant.city = :city ORDER BY d.createdAt ASC")
    List<Delivery> findAvailableDeliveriesInCity(@Param("city") String city);

    /**
     * Find active deliveries by partner
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId " +
           "AND d.status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'OUT_FOR_DELIVERY') ORDER BY d.createdAt DESC")
    List<Delivery> findActiveDeliveriesByPartner(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find deliveries by partner and status
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId AND d.status = :status ORDER BY d.createdAt DESC")
    List<Delivery> findByDeliveryPartnerIdAndStatus(@Param("deliveryPartnerId") Long deliveryPartnerId, @Param("status") DeliveryStatus status);

    /**
     * Find completed deliveries by partner between dates
     */
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId " +
           "AND d.status = 'DELIVERED' AND d.actualDeliveryTime BETWEEN :startDate AND :endDate " +
           "ORDER BY d.actualDeliveryTime DESC")
    List<Delivery> findCompletedDeliveriesByPartnerBetweenDates(
            @Param("deliveryPartnerId") Long deliveryPartnerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate delivery partner earnings between dates
     */
    @Query("SELECT COALESCE(SUM(d.deliveryFee), 0) FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId " +
           "AND d.status = 'DELIVERED' AND d.actualDeliveryTime BETWEEN :startDate AND :endDate")
    BigDecimal calculatePartnerEarnings(@Param("deliveryPartnerId") Long deliveryPartnerId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find delayed deliveries (past estimated time)
     */
    @Query("SELECT d FROM Delivery d WHERE d.estimatedDeliveryTime < CURRENT_TIMESTAMP " +
           "AND d.status IN ('PICKED_UP', 'OUT_FOR_DELIVERY') ORDER BY d.estimatedDeliveryTime ASC")
    List<Delivery> findDelayedDeliveries();

    /**
     * Find deliveries ready for pickup
     */
    @Query("SELECT d FROM Delivery d WHERE d.order.status = 'READY_FOR_PICKUP' " +
           "AND d.status = 'ASSIGNED' ORDER BY d.order.updatedAt ASC")
    List<Delivery> findDeliveriesReadyForPickup();

    /**
     * Find deliveries in progress
     */
    @Query("SELECT d FROM Delivery d WHERE d.status IN ('ACCEPTED', 'PICKED_UP', 'OUT_FOR_DELIVERY') ORDER BY d.updatedAt ASC")
    List<Delivery> findDeliveriesInProgress();

    /**
     * Count deliveries by status
     */
    long countByStatus(DeliveryStatus status);

    /**
     * Count deliveries by partner
     */
    long countByDeliveryPartnerId(Long deliveryPartnerId);

    /**
     * Count completed deliveries by partner
     */
    long countByDeliveryPartnerIdAndStatus(Long deliveryPartnerId, DeliveryStatus status);

    /**
     * Find deliveries between dates
     */
    @Query("SELECT d FROM Delivery d WHERE d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
    List<Delivery> findDeliveriesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find top delivery partners by delivery count
     */
    @Query("SELECT d.deliveryPartner FROM Delivery d WHERE d.status = 'DELIVERED' " +
           "GROUP BY d.deliveryPartner ORDER BY COUNT(d) DESC")
    List<Object> findTopDeliveryPartners();

    /**
     * Find average delivery time for partner
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, d.actualPickupTime, d.actualDeliveryTime)) " +
           "FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId AND d.status = 'DELIVERED' " +
           "AND d.actualPickupTime IS NOT NULL AND d.actualDeliveryTime IS NOT NULL")
    Double findAverageDeliveryTimeByPartner(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find deliveries with customer ratings
     */
    @Query("SELECT d FROM Delivery d WHERE d.customerRating IS NOT NULL ORDER BY d.actualDeliveryTime DESC")
    List<Delivery> findDeliveriesWithRatings();

    /**
     * Find average rating for delivery partner
     */
    @Query("SELECT AVG(d.customerRating) FROM Delivery d WHERE d.deliveryPartner.id = :deliveryPartnerId AND d.customerRating IS NOT NULL")
    Double findAverageRatingByPartner(@Param("deliveryPartnerId") Long deliveryPartnerId);

    /**
     * Find deliveries needing partner assignment
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'ASSIGNED' AND d.deliveryPartner IS NULL " +
           "AND d.createdAt < :before ORDER BY d.createdAt ASC")
    List<Delivery> findDeliveriesNeedingAssignment(@Param("before") LocalDateTime before);

    /**
     * Find cancelled deliveries
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'CANCELLED' ORDER BY d.cancelledAt DESC")
    List<Delivery> findCancelledDeliveries();

    /**
     * Calculate total delivery fees between dates
     */
    @Query("SELECT COALESCE(SUM(d.deliveryFee), 0) FROM Delivery d WHERE d.status = 'DELIVERED' " +
           "AND d.actualDeliveryTime BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalDeliveryFees(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}