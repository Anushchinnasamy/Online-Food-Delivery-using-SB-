package com.fooddelivery.repository;

import com.fooddelivery.entity.Payment;
import com.fooddelivery.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payment by order ID
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    /**
     * Find payments between dates
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find successful payments between dates
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCESS' AND p.processedAt BETWEEN :startDate AND :endDate ORDER BY p.processedAt DESC")
    List<Payment> findSuccessfulPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total successful payments amount between dates
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' AND p.processedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateSuccessfulPaymentsTotal(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find failed payments for retry
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Payment> findFailedPaymentsForRetry(@Param("since") LocalDateTime since);

    /**
     * Find pending payments (might be stuck)
     */
    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'PROCESSING') AND p.createdAt < :before ORDER BY p.createdAt ASC")
    List<Payment> findStuckPayments(@Param("before") LocalDateTime before);

    /**
     * Find payments by customer
     */
    @Query("SELECT p FROM Payment p WHERE p.order.customer.id = :customerId ORDER BY p.createdAt DESC")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find payments by restaurant
     */
    @Query("SELECT p FROM Payment p WHERE p.order.restaurant.id = :restaurantId AND p.status = 'SUCCESS' ORDER BY p.processedAt DESC")
    List<Payment> findSuccessfulPaymentsByRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Calculate restaurant earnings between dates
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.restaurant.id = :restaurantId AND p.status = 'SUCCESS' AND p.processedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRestaurantEarnings(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find refunded payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') ORDER BY p.refundedAt DESC")
    List<Payment> findRefundedPayments();

    /**
     * Calculate total refunds between dates
     */
    @Query("SELECT COALESCE(SUM(p.refundAmount), 0) FROM Payment p WHERE p.refundedAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRefunds(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count payments by status
     */
    long countByStatus(PaymentStatus status);

    /**
     * Find high-value payments
     */
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount ORDER BY p.amount DESC")
    List<Payment> findHighValuePayments(@Param("minAmount") BigDecimal minAmount);

    /**
     * Find recent payments
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(@Param("since") LocalDateTime since);

    /**
     * Find payments by method and status
     */
    @Query("SELECT p FROM Payment p WHERE p.method = :method AND p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByMethodAndStatus(@Param("method") String method, @Param("status") PaymentStatus status);
}