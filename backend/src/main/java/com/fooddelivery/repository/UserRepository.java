package com.fooddelivery.repository;

import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Find user by phone number
     */
    Optional<User> findByPhone(String phone);

    /**
     * Find active user by email
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.isActive = true AND u.deletedAt IS NULL")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    /**
     * Find users by role
     */
    List<User> findByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role, Pageable pageable);

    /**
     * Find active users by city
     */
    @Query("SELECT u FROM User u WHERE u.city = :city AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findActiveUsersByCity(@Param("city") String city);

    /**
     * Find delivery partners in a specific city
     */
    @Query("SELECT u FROM User u WHERE u.role = 'DELIVERY_PARTNER' AND u.city = :city AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findDeliveryPartnersByCity(@Param("city") String city);

    /**
     * Find available delivery partners (not currently on delivery)
     */
    @Query("SELECT u FROM User u WHERE u.role = 'DELIVERY_PARTNER' AND u.isActive = true AND u.deletedAt IS NULL " +
           "AND u.id NOT IN (SELECT d.deliveryPartner.id FROM Delivery d WHERE d.status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'OUT_FOR_DELIVERY'))")
    List<User> findAvailableDeliveryPartners();

    /**
     * Check if email exists (excluding soft deleted users)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if phone exists (excluding soft deleted users)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.deletedAt IS NULL")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * Find users by name containing (case-insensitive search)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Count users by role
     */
    long countByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role);

    /**
     * Find users created in the last N days
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_TIMESTAMP - :days DAY AND u.deletedAt IS NULL")
    List<User> findUsersCreatedInLastDays(@Param("days") int days);

    /**
     * Find users within a geographic radius (simplified - in real app would use PostGIS)
     */
    @Query("SELECT u FROM User u WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL " +
           "AND u.isActive = true AND u.deletedAt IS NULL " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * cos(radians(u.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(u.latitude)))) <= :radiusKm")
    List<User> findUsersWithinRadius(@Param("lat") double latitude, @Param("lng") double longitude, @Param("radiusKm") double radiusKm);

    /**
     * Find restaurant owners
     */
    @Query("SELECT u FROM User u WHERE u.role = 'RESTAURANT_ADMIN' AND u.restaurant IS NOT NULL AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findRestaurantOwners();

    /**
     * Find customers with orders
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o WHERE u.role = 'CUSTOMER' AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findCustomersWithOrders();

    /**
     * Find top customers by order count
     */
    @Query("SELECT u FROM User u JOIN u.orders o WHERE u.role = 'CUSTOMER' AND u.isActive = true AND u.deletedAt IS NULL " +
           "GROUP BY u ORDER BY COUNT(o) DESC")
    List<User> findTopCustomersByOrderCount(Pageable pageable);
}