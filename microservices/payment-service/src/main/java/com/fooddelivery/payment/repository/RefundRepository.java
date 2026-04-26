package com.fooddelivery.payment.repository;

import com.fooddelivery.payment.entity.Refund;
import com.fooddelivery.payment.enums.RefundStatus;
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
 * Repository interface for Refund entity
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    /**
     * Find refund by transaction ID
     */
    Optional<Refund> findByRefundTransactionId(String refundTransactionId);

    /**
     * Find refund by gateway refund ID
     */
    Optional<Refund> findByGatewayRefundId(String gatewayRefundId);

    /**
     * Find refunds by payment ID
     */
    List<Refund> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    /**
     * Find refunds by payment transaction ID
     */
    @Query("SELECT r FROM Refund r WHERE r.payment.transactionId = :transactionId ORDER BY r.createdAt DESC")
    List<Refund> findByPaymentTransactionId(@Param("transactionId") String transactionId);

    /**
     * Find refunds by status
     */
    List<Refund> findByRefundStatus(RefundStatus refundStatus);

    /**
     * Find refunds by status and created date range
     */
    @Query("SELECT r FROM Refund r WHERE r.refundStatus = :status AND r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<Refund> findByStatusAndDateRange(
        @Param("status") RefundStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find pending refunds older than specified time
     */
    @Query("SELECT r FROM Refund r WHERE r.refundStatus IN ('INITIATED', 'PROCESSING') AND r.createdAt < :cutoffTime")
    List<Refund> findPendingRefundsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find refunds by user ID (through payment)
     */
    @Query("SELECT r FROM Refund r WHERE r.payment.userId = :userId ORDER BY r.createdAt DESC")
    Page<Refund> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find refunds initiated by specific entity
     */
    List<Refund> findByInitiatedByOrderByCreatedAtDesc(String initiatedBy);

    /**
     * Count refunds by status
     */
    long countByRefundStatus(RefundStatus refundStatus);

    /**
     * Find refunds by multiple statuses
     */
    @Query("SELECT r FROM Refund r WHERE r.refundStatus IN :statuses ORDER BY r.createdAt DESC")
    List<Refund> findByRefundStatusIn(@Param("statuses") List<RefundStatus> statuses);

    /**
     * Check if refund exists for payment
     */
    boolean existsByPaymentId(Long paymentId);

    /**
     * Find successful refunds for payment
     */
    @Query("SELECT r FROM Refund r WHERE r.payment.id = :paymentId AND r.refundStatus = 'SUCCESS' ORDER BY r.createdAt DESC")
    List<Refund> findSuccessfulRefundsByPaymentId(@Param("paymentId") Long paymentId);
}