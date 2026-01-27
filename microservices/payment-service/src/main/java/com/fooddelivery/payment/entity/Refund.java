package com.fooddelivery.payment.entity;

import com.fooddelivery.payment.enums.RefundStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund entity representing a refund transaction
 */
@Entity
@Table(name = "refunds", indexes = {
    @Index(name = "idx_refund_transaction_id", columnList = "refundTransactionId", unique = true),
    @Index(name = "idx_refund_payment", columnList = "payment_id"),
    @Index(name = "idx_refund_status", columnList = "refundStatus"),
    @Index(name = "idx_refund_created", columnList = "createdAt"),
    @Index(name = "idx_refund_gateway_ref", columnList = "gatewayRefundId")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Refund transaction ID is required")
    @Size(max = 100, message = "Refund transaction ID cannot exceed 100 characters")
    @Column(name = "refund_transaction_id", nullable = false, unique = true, length = 100)
    private String refundTransactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Refund amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Refund amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus refundStatus = RefundStatus.INITIATED;

    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason cannot exceed 500 characters")
    @Column(name = "refund_reason", nullable = false, length = 500)
    private String refundReason;

    @Size(max = 100, message = "Gateway refund ID cannot exceed 100 characters")
    @Column(name = "gateway_refund_id", length = 100)
    private String gatewayRefundId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Size(max = 500, message = "Failure reason cannot exceed 500 characters")
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "initiated_by", length = 50)
    private String initiatedBy; // USER, RESTAURANT, ADMIN, SYSTEM

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Business logic methods
    public void updateStatus(RefundStatus newStatus) {
        if (!refundStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", refundStatus, newStatus)
            );
        }
        this.refundStatus = newStatus;
        
        if (newStatus.isFinalState()) {
            this.processedAt = LocalDateTime.now();
        }
    }

    public void markAsSuccessful(String gatewayRefundId, String gatewayResponse) {
        updateStatus(RefundStatus.SUCCESS);
        this.gatewayRefundId = gatewayRefundId;
        this.gatewayResponse = gatewayResponse;
        
        // Update parent payment's refunded amount
        if (payment != null) {
            payment.updateRefundedAmount(refundAmount);
        }
    }

    public void markAsFailed(String failureReason, String gatewayResponse) {
        updateStatus(RefundStatus.FAILED);
        this.failureReason = failureReason;
        this.gatewayResponse = gatewayResponse;
    }

    public boolean isInFinalState() {
        return refundStatus.isFinalState();
    }

    public boolean isSuccessful() {
        return refundStatus.isSuccessful();
    }
}