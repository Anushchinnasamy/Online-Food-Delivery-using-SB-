package com.fooddelivery.payment.entity;

import com.fooddelivery.payment.enums.PaymentMethod;
import com.fooddelivery.payment.enums.PaymentStatus;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Payment entity representing a payment transaction
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_transaction_id", columnList = "transactionId", unique = true),
    @Index(name = "idx_payment_order", columnList = "orderId"),
    @Index(name = "idx_payment_user", columnList = "userId"),
    @Index(name = "idx_payment_status", columnList = "paymentStatus"),
    @Index(name = "idx_payment_method", columnList = "paymentMethod"),
    @Index(name = "idx_payment_created", columnList = "createdAt"),
    @Index(name = "idx_payment_gateway_ref", columnList = "gatewayTransactionId")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction ID is required")
    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.INITIATED;

    @Size(max = 100, message = "Gateway transaction ID cannot exceed 100 characters")
    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Size(max = 50, message = "Gateway name cannot exceed 50 characters")
    @Column(name = "gateway_name", length = 50)
    private String gatewayName;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Size(max = 500, message = "Failure reason cannot exceed 500 characters")
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_amount", precision = 12, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-many relationship with Refund
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Refund> refunds = new ArrayList<>();

    // Business logic methods
    public void updateStatus(PaymentStatus newStatus) {
        if (!paymentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", paymentStatus, newStatus)
            );
        }
        this.paymentStatus = newStatus;
        
        if (newStatus.isFinalState()) {
            this.processedAt = LocalDateTime.now();
        }
    }

    public void markAsSuccessful(String gatewayTransactionId, String gatewayResponse) {
        updateStatus(PaymentStatus.SUCCESS);
        this.gatewayTransactionId = gatewayTransactionId;
        this.gatewayResponse = gatewayResponse;
    }

    public void markAsFailed(String failureReason, String gatewayResponse) {
        updateStatus(PaymentStatus.FAILED);
        this.failureReason = failureReason;
        this.gatewayResponse = gatewayResponse;
    }

    public boolean canBeRefunded() {
        return paymentStatus.canBeRefunded() && 
               refundedAmount.compareTo(amount) < 0;
    }

    public BigDecimal getRefundableAmount() {
        return amount.subtract(refundedAmount);
    }

    public void addRefund(Refund refund) {
        refunds.add(refund);
        refund.setPayment(this);
    }

    public void updateRefundedAmount(BigDecimal refundAmount) {
        this.refundedAmount = this.refundedAmount.add(refundAmount);
        
        // Update payment status based on refunded amount
        if (refundedAmount.compareTo(amount) == 0) {
            this.paymentStatus = PaymentStatus.REFUNDED;
        } else if (refundedAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.paymentStatus = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }

    public boolean isFullyRefunded() {
        return refundedAmount.compareTo(amount) == 0;
    }

    public boolean isPartiallyRefunded() {
        return refundedAmount.compareTo(BigDecimal.ZERO) > 0 && 
               refundedAmount.compareTo(amount) < 0;
    }
}