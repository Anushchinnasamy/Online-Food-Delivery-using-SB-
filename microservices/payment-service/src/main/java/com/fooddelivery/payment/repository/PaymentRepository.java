package com.fooddelivery.payment.repository;

import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.enums.PaymentStatus;
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
 * Repository interface for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payment by gateway transaction ID
     */
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    /**
     * Find payments by order ID
     */
    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    /**
     * Find payments by user ID with pagination
     */
    Page<Payment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find payments by user ID and status
     */
    List<Payment> findByUserIdAndPaymentStatusOrderByCreatedAtDesc(Long userId, PaymentStatus paymentStatus);

    /**
     * Find payments by status
     */
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Find payments by status and created date range
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = :status AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findByStatusAndDateRange(
        @Param("status") PaymentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus IN ('INITIATED', 'PROCESSING') AND p.createdAt < :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Check if payment exists for order
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Check if successful payment exists for order
     */
    boolean existsByOrderIdAndPaymentStatus(Long orderId, PaymentStatus paymentStatus);

    /**
     * Count payments by status
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Find payments by multiple statuses
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus IN :statuses ORDER BY p.createdAt DESC")
    List<Payment> findByPaymentStatusIn(@Param("statuses") List<PaymentStatus> statuses);

    /**
     * Find user's recent payments
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);
}