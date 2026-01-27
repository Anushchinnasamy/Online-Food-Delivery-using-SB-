package com.fooddelivery.delivery.entity;

import com.fooddelivery.delivery.enums.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Delivery entity representing a delivery assignment and tracking
 */
@Entity
@Table(name = "deliveries", indexes = {
    @Index(name = "idx_delivery_order", columnList = "orderId", unique = true),
    @Index(name = "idx_delivery_partner", columnList = "deliveryPartnerId"),
    @Index(name = "idx_delivery_status", columnList = "deliveryStatus"),
    @Index(name = "idx_delivery_assigned", columnList = "assignedAt"),
    @Index(name = "idx_delivery_partner_status", columnList = "deliveryPartnerId, deliveryStatus"),
    @Index(name = "idx_delivery_created", columnList = "createdAt")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @NotNull(message = "Delivery Partner ID is required")
    @Column(name = "delivery_partner_id", nullable = false)
    private Long deliveryPartnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus = DeliveryStatus.ASSIGNED;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "on_the_way_at")
    private LocalDateTime onTheWayAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "actual_delivery_time_minutes")
    private Integer actualDeliveryTimeMinutes;

    @Column(name = "rejection_count")
    private Integer rejectionCount = 0;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "delivery_notes", length = 1000)
    private String deliveryNotes;

    @Column(name = "customer_rating")
    private Integer customerRating;

    @Column(name = "customer_feedback", length = 1000)
    private String customerFeedback;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Business logic methods
    public void updateStatus(DeliveryStatus newStatus) {
        if (!deliveryStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", deliveryStatus, newStatus)
            );
        }
        
        DeliveryStatus previousStatus = this.deliveryStatus;
        this.deliveryStatus = newStatus;
        
        // Set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case ACCEPTED -> {
                this.acceptedAt = now;
                calculateEstimatedDeliveryTime();
            }
            case PICKED_UP -> this.pickedUpAt = now;
            case ON_THE_WAY -> this.onTheWayAt = now;
            case DELIVERED -> {
                this.deliveredAt = now;
                calculateActualDeliveryTime();
            }
            case CANCELLED -> this.cancelledAt = now;
        }
    }

    public void accept() {
        updateStatus(DeliveryStatus.ACCEPTED);
    }

    public void reject(String reason) {
        if (!deliveryStatus.canBeRejected()) {
            throw new IllegalStateException("Delivery cannot be rejected in current state: " + deliveryStatus);
        }
        
        this.rejectionCount++;
        this.rejectionReason = reason;
        // Status will be updated by the service to reassign
    }

    public void cancel(String reason) {
        if (!deliveryStatus.canBeCancelled()) {
            throw new IllegalStateException("Delivery cannot be cancelled in current state: " + deliveryStatus);
        }
        
        this.cancellationReason = reason;
        updateStatus(DeliveryStatus.CANCELLED);
    }

    public void markAsPickedUp() {
        updateStatus(DeliveryStatus.PICKED_UP);
    }

    public void markAsOnTheWay() {
        updateStatus(DeliveryStatus.ON_THE_WAY);
    }

    public void markAsDelivered() {
        updateStatus(DeliveryStatus.DELIVERED);
    }

    public boolean isInFinalState() {
        return deliveryStatus.isFinalState();
    }

    public boolean canBeReassigned() {
        return rejectionCount < 3 && !isInFinalState();
    }

    public void reassign(Long newDeliveryPartnerId) {
        if (!canBeReassigned()) {
            throw new IllegalStateException("Delivery cannot be reassigned");
        }
        
        this.deliveryPartnerId = newDeliveryPartnerId;
        this.deliveryStatus = DeliveryStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        
        // Clear previous timestamps
        this.acceptedAt = null;
        this.pickedUpAt = null;
        this.onTheWayAt = null;
        this.deliveredAt = null;
        this.cancelledAt = null;
    }

    public void addCustomerFeedback(Integer rating, String feedback) {
        if (deliveryStatus == DeliveryStatus.DELIVERED) {
            this.customerRating = rating;
            this.customerFeedback = feedback;
        }
    }

    public void addDeliveryNotes(String notes) {
        this.deliveryNotes = notes;
    }

    private void calculateEstimatedDeliveryTime() {
        if (acceptedAt != null) {
            // Estimate 30 minutes from acceptance
            this.estimatedDeliveryTime = acceptedAt.plusMinutes(30);
        }
    }

    private void calculateActualDeliveryTime() {
        if (assignedAt != null && deliveredAt != null) {
            long minutes = java.time.Duration.between(assignedAt, deliveredAt).toMinutes();
            this.actualDeliveryTimeMinutes = (int) minutes;
        }
    }

    public boolean isDelayed() {
        return estimatedDeliveryTime != null && 
               LocalDateTime.now().isAfter(estimatedDeliveryTime) && 
               !isInFinalState();
    }

    public long getDelayMinutes() {
        if (estimatedDeliveryTime != null && isDelayed()) {
            return java.time.Duration.between(estimatedDeliveryTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }
}