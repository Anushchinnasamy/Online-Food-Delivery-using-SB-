package com.fooddelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Delivery entity representing delivery assignments and tracking
 */
@Entity
@Table(name = "deliveries", indexes = {
    @Index(name = "idx_delivery_order", columnList = "order_id"),
    @Index(name = "idx_delivery_partner", columnList = "delivery_partner_id"),
    @Index(name = "idx_delivery_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 20)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;

    @Column(name = "pickup_address", nullable = false, length = 500)
    private String pickupAddress;

    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "delivery_latitude")
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude")
    private Double deliveryLongitude;

    @Column(name = "estimated_distance_km", precision = 5, scale = 2)
    private BigDecimal estimatedDistanceKm;

    @Column(name = "actual_distance_km", precision = 5, scale = 2)
    private BigDecimal actualDistanceKm;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;

    @Column(name = "actual_pickup_time")
    private LocalDateTime actualPickupTime;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Column(name = "delivery_instructions", length = 500)
    private String deliveryInstructions;

    @Column(name = "customer_rating")
    private Integer customerRating;

    @Column(name = "customer_feedback", length = 500)
    private String customerFeedback;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private User deliveryPartner;

    // Constructors
    public Delivery() {}

    public Delivery(Order order) {
        this.order = order;
        this.trackingNumber = generateTrackingNumber();
        this.pickupAddress = order.getRestaurant().getAddress();
        this.pickupLatitude = order.getRestaurant().getLatitude();
        this.pickupLongitude = order.getRestaurant().getLongitude();
        this.deliveryAddress = order.getDeliveryAddress();
        this.deliveryLatitude = order.getDeliveryLatitude();
        this.deliveryLongitude = order.getDeliveryLongitude();
        this.deliveryFee = order.getDeliveryFee();
        this.deliveryInstructions = order.getSpecialInstructions();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public Double getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(Double pickupLatitude) { this.pickupLatitude = pickupLatitude; }

    public Double getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(Double pickupLongitude) { this.pickupLongitude = pickupLongitude; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Double getDeliveryLatitude() { return deliveryLatitude; }
    public void setDeliveryLatitude(Double deliveryLatitude) { this.deliveryLatitude = deliveryLatitude; }

    public Double getDeliveryLongitude() { return deliveryLongitude; }
    public void setDeliveryLongitude(Double deliveryLongitude) { this.deliveryLongitude = deliveryLongitude; }

    public BigDecimal getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public void setEstimatedDistanceKm(BigDecimal estimatedDistanceKm) { this.estimatedDistanceKm = estimatedDistanceKm; }

    public BigDecimal getActualDistanceKm() { return actualDistanceKm; }
    public void setActualDistanceKm(BigDecimal actualDistanceKm) { this.actualDistanceKm = actualDistanceKm; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public LocalDateTime getEstimatedPickupTime() { return estimatedPickupTime; }
    public void setEstimatedPickupTime(LocalDateTime estimatedPickupTime) { this.estimatedPickupTime = estimatedPickupTime; }

    public LocalDateTime getActualPickupTime() { return actualPickupTime; }
    public void setActualPickupTime(LocalDateTime actualPickupTime) { this.actualPickupTime = actualPickupTime; }

    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { this.actualDeliveryTime = actualDeliveryTime; }

    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

    public Integer getCustomerRating() { return customerRating; }
    public void setCustomerRating(Integer customerRating) { this.customerRating = customerRating; }

    public String getCustomerFeedback() { return customerFeedback; }
    public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public User getDeliveryPartner() { return deliveryPartner; }
    public void setDeliveryPartner(User deliveryPartner) { this.deliveryPartner = deliveryPartner; }

    // Utility methods
    public void assignToPartner(User deliveryPartner) {
        if (deliveryPartner.getRole() != UserRole.DELIVERY_PARTNER) {
            throw new IllegalArgumentException("User must be a delivery partner");
        }
        this.deliveryPartner = deliveryPartner;
        this.status = DeliveryStatus.ASSIGNED;
    }

    public void acceptDelivery() {
        if (status != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Delivery must be in ASSIGNED status to be accepted");
        }
        this.status = DeliveryStatus.ACCEPTED;
    }

    public void pickupOrder() {
        if (status != DeliveryStatus.ACCEPTED) {
            throw new IllegalStateException("Delivery must be in ACCEPTED status to be picked up");
        }
        this.status = DeliveryStatus.PICKED_UP;
        this.actualPickupTime = LocalDateTime.now();
    }

    public void startDelivery() {
        if (status != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException("Order must be picked up before starting delivery");
        }
        this.status = DeliveryStatus.OUT_FOR_DELIVERY;
    }

    public void completeDelivery() {
        if (status != DeliveryStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Delivery must be out for delivery to be completed");
        }
        this.status = DeliveryStatus.DELIVERED;
        this.actualDeliveryTime = LocalDateTime.now();
    }

    public void cancelDelivery(String reason) {
        if (status == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a completed delivery");
        }
        this.status = DeliveryStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public boolean canBeAccepted() {
        return status == DeliveryStatus.ASSIGNED;
    }

    public boolean canBePickedUp() {
        return status == DeliveryStatus.ACCEPTED;
    }

    public boolean canBeDelivered() {
        return status == DeliveryStatus.OUT_FOR_DELIVERY;
    }

    public void rateDelivery(Integer rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.customerRating = rating;
        this.customerFeedback = feedback;
    }

    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", status=" + status +
                ", deliveryPartner=" + (deliveryPartner != null ? deliveryPartner.getName() : "Unassigned") +
                ", createdAt=" + createdAt +
                '}';
    }
}