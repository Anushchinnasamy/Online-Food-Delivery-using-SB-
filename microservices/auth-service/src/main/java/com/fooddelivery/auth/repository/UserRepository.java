package com.fooddelivery.auth.repository;

import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.entity.UserRole;
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
     * Find active user by phone
     */
    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.isActive = true AND u.deletedAt IS NULL")
    Optional<User> findActiveUserByPhone(@Param("phone") String phone);

    /**
     * Check if email exists (case-insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if phone exists
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.deletedAt IS NULL")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * Find users by role
     */
    List<User> findByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role, Pageable pageable);

    /**
     * Find all active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    List<User> findAllActiveUsers();

    /**
     * Find all active users with pagination
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    Page<User> findAllActiveUsers(Pageable pageable);

    /**
     * Find users created after a specific date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Find users by verification status
     */
    @Query("SELECT u FROM User u WHERE u.isVerified = :verified AND u.isActive = true AND u.deletedAt IS NULL")
    List<User> findByVerificationStatus(@Param("verified") boolean verified);

    /**
     * Find locked users
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil > :now AND u.deletedAt IS NULL")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);

    /**
     * Update last login time
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Reset failed login attempts
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void resetFailedLoginAttempts(@Param("userId") Long userId);

    /**
     * Increment failed login attempts
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.id = :userId")
    void incrementFailedLoginAttempts(@Param("userId") Long userId);

    /**
     * Lock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockUntil WHERE u.id = :userId")
    void lockUserAccount(@Param("userId") Long userId, @Param("lockUntil") LocalDateTime lockUntil);

    /**
     * Unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = NULL, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void unlockUserAccount(@Param("userId") Long userId);

    /**
     * Soft delete user
     */
    @Modifying
    @Query("UPDATE User u SET u.deletedAt = :deletedAt, u.isActive = false WHERE u.id = :userId")
    void softDeleteUser(@Param("userId") Long userId, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true AND u.deletedAt IS NULL")
    long countByRole(@Param("role") UserRole role);

    /**
     * Count total active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL")
    long countActiveUsers();

    /**
     * Count verified users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true AND u.isActive = true AND u.deletedAt IS NULL")
    long countVerifiedUsers();
}