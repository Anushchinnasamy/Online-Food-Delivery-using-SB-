package com.fooddelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity representing payment transactions for orders
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_transaction_id", columnList = "transaction_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "gateway_response", length = 1000)
    private String gatewayResponse;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "refund_transaction_id", length = 100)
    private String refundTransactionId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Constructors
    public Payment() {}

    public Payment(Order order, BigDecimal amount, PaymentMethod method) {
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.transactionId = generateTransactionId();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundTransactionId() { return refundTransactionId; }
    public void setRefundTransactionId(String refundTransactionId) { this.refundTransactionId = refundTransactionId; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    // Utility methods
    public void markAsSuccessful(String gatewayResponse) {
        this.status = PaymentStatus.SUCCESS;
        this.gatewayResponse = gatewayResponse;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.processedAt = LocalDateTime.now();
    }

    public void processRefund(BigDecimal refundAmount) {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot refund a payment that was not successful");
        }
        
        if (refundAmount.compareTo(this.amount) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed payment amount");
        }
        
        this.refundAmount = refundAmount;
        this.refundTransactionId = generateRefundTransactionId();
        this.refundedAt = LocalDateTime.now();
        
        if (refundAmount.equals(this.amount)) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }

    public boolean canBeRefunded() {
        return status == PaymentStatus.SUCCESS || status == PaymentStatus.PARTIALLY_REFUNDED;
    }

    public BigDecimal getRemainingRefundableAmount() {
        return amount.subtract(refundAmount);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private String generateRefundTransactionId() {
        return "REF" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", method=" + method +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}