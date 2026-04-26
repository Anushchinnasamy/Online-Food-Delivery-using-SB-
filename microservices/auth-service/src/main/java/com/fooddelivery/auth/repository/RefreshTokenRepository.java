package com.fooddelivery.auth.repository;

import com.fooddelivery.auth.entity.RefreshToken;
import com.fooddelivery.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entity operations
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token string
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find valid refresh token by token string
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.isRevoked = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Find all refresh tokens for a user
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Find all valid refresh tokens for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Find refresh tokens by user ID
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId")
    List<RefreshToken> findByUserId(@Param("userId") Long userId);

    /**
     * Find expired tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt <= :now")
    List<RefreshToken> findExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Find tokens expiring soon
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt BETWEEN :now AND :threshold AND rt.isRevoked = false")
    List<RefreshToken> findTokensExpiringSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);

    /**
     * Revoke all tokens for a user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user")
    void revokeAllTokensForUser(@Param("user") User user);

    /**
     * Revoke all tokens for a user by ID
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user.id = :userId")
    void revokeAllTokensForUserId(@Param("userId") Long userId);

    /**
     * Revoke token by token string
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    void revokeToken(@Param("token") String token);

    /**
     * Delete expired tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt <= :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete revoked tokens older than specified date
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.isRevoked = true AND rt.createdAt <= :date")
    void deleteOldRevokedTokens(@Param("date") LocalDateTime date);

    /**
     * Count valid tokens for a user
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false AND rt.expiresAt > :now")
    long countValidTokensForUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Count total valid tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.isRevoked = false AND rt.expiresAt > :now")
    long countValidTokens(@Param("now") LocalDateTime now);

    /**
     * Find tokens by IP address
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.ipAddress = :ipAddress AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByIpAddress(@Param("ipAddress") String ipAddress, @Param("now") LocalDateTime now);

    /**
     * Find tokens by device info
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.deviceInfo = :deviceInfo AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByDeviceInfo(@Param("deviceInfo") String deviceInfo, @Param("now") LocalDateTime now);
}