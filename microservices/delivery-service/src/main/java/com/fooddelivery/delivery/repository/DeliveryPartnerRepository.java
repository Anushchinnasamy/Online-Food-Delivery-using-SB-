package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.enums.VehicleType;
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
 * Repository interface for DeliveryPartner entity
 */
@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

    /**
     * Find delivery partner by phone number
     */
    Optional<DeliveryPartner> findByPhone(String phone);

    /**
     * Find active delivery partners
     */
    List<DeliveryPartner> findByIsActiveTrue();

    /**
     * Find available delivery partners
     */
    List<DeliveryPartner> findByIsActiveTrueAndIsAvailableTrue();

    /**
     * Find delivery partners by vehicle type
     */
    List<DeliveryPartner> findByVehicleTypeAndIsActiveTrueAndIsAvailableTrue(VehicleType vehicleType);

    /**
     * Find delivery partners with pagination
     */
    Page<DeliveryPartner> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Find active delivery partners with pagination
     */
    Page<DeliveryPartner> findByIsActiveTrueAndDeletedAtIsNull(Pageable pageable);

    /**
     * Check if phone number exists
     */
    boolean existsByPhone(String phone);

    /**
     * Check if phone number exists for different partner
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);

    /**
     * Find online delivery partners (active in last 15 minutes)
     */
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.isActive = true AND dp.isAvailable = true " +
           "AND dp.deletedAt IS NULL AND dp.lastActiveAt > :cutoffTime")
    List<DeliveryPartner> findOnlineDeliveryPartners(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find delivery partners by location within radius
     */
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.isActive = true AND dp.isAvailable = true " +
           "AND dp.deletedAt IS NULL AND dp.currentLatitude IS NOT NULL AND dp.currentLongitude IS NOT NULL " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(dp.currentLatitude)) * " +
           "cos(radians(dp.currentLongitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(dp.currentLatitude)))) <= :radiusKm")
    List<DeliveryPartner> findDeliveryPartnersWithinRadius(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm
    );

    /**
     * Find top rated delivery partners
     */
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.isActive = true AND dp.isAvailable = true " +
           "AND dp.deletedAt IS NULL AND dp.totalDeliveries >= :minDeliveries " +
           "ORDER BY dp.rating DESC, dp.successfulDeliveries DESC")
    List<DeliveryPartner> findTopRatedDeliveryPartners(@Param("minDeliveries") Integer minDeliveries, Pageable pageable);

    /**
     * Count active delivery partners
     */
    long countByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Count available delivery partners
     */
    long countByIsActiveTrueAndIsAvailableTrueAndDeletedAtIsNull();

    /**
     * Find delivery partners by rating range
     */
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.isActive = true AND dp.deletedAt IS NULL " +
           "AND dp.rating BETWEEN :minRating AND :maxRating ORDER BY dp.rating DESC")
    List<DeliveryPartner> findByRatingRange(@Param("minRating") Double minRating, @Param("maxRating") Double maxRating);

    /**
     * Find delivery partners with high success rate
     */
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.isActive = true AND dp.isAvailable = true " +
           "AND dp.deletedAt IS NULL AND dp.totalDeliveries >= :minDeliveries " +
           "AND (CAST(dp.successfulDeliveries AS double) / dp.totalDeliveries) >= :minSuccessRate")
    List<DeliveryPartner> findHighPerformingDeliveryPartners(
        @Param("minDeliveries") Integer minDeliveries,
        @Param("minSuccessRate") Double minSuccessRate
    );
}