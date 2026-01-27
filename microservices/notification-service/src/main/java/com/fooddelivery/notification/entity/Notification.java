package com.fooddelivery.notification.entity;

import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification entity representing a notification sent to a user
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "userId"),
    @Index(name = "idx_notification_reference", columnList = "referenceId"),
    @Index(name = "idx_notification_type", columnList = "notificationType"),
    @Index(name = "idx_notification_event", columnList = "eventType"),
    @Index(name = "idx_notification_status", columnList = "status"),
    @Index(name = "idx_notification_created", columnList = "createdAt"),
    @Index(name = "idx_notification_user_status", columnList = "userId, status"),
    @Index(name = "idx_notification_status_created", columnList = "status, createdAt")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reference_id")
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "recipient_email", length = 150)
    private String recipientEmail;

    @Column(name = "recipient_phone", length = 15)
    private String recipientPhone;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "template_name", length = 100)
    private String templateName;

    @Column(name = "template_data", columnDefinition = "TEXT")
    private String templateData;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retry_attempts")
    private Integer maxRetryAttempts = 3;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "external_id", length = 100)
    private String externalId;

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
    public void updateStatus(NotificationStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
        
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case SENT -> this.sentAt = now;
            case FAILED -> this.failedAt = now;
            case RETRYING -> this.calculateNextRetryTime();
        }
    }

    public void markAsSent() {
        updateStatus(NotificationStatus.SENT);
    }

    public void markAsFailed(String reason) {
        this.failureReason = reason;
        updateStatus(NotificationStatus.FAILED);
    }

    public void markAsRetrying() {
        this.retryCount++;
        updateStatus(NotificationStatus.RETRYING);
    }

    public boolean canBeRetried() {
        return status.canBeRetried() && retryCount < maxRetryAttempts;
    }

    public boolean isReadyForRetry() {
        return status == NotificationStatus.RETRYING && 
               nextRetryAt != null && 
               LocalDateTime.now().isAfter(nextRetryAt);
    }

    public boolean hasExceededMaxRetries() {
        return retryCount >= maxRetryAttempts;
    }

    public void calculateNextRetryTime() {
        if (retryCount > 0) {
            // Exponential backoff: 1min, 5min, 15min
            int delayMinutes = switch (retryCount) {
                case 1 -> 1;
                case 2 -> 5;
                default -> 15;
            };
            this.nextRetryAt = LocalDateTime.now().plusMinutes(delayMinutes);
        }
    }

    public boolean isCritical() {
        return eventType.isCritical();
    }

    public boolean isUserFacing() {
        return eventType.isUserFacing();
    }

    public boolean requiresExternalService() {
        return notificationType.requiresExternalService();
    }

    public boolean isRealTime() {
        return notificationType.isRealTime();
    }

    public void setRecipientInfo(String email, String phone) {
        this.recipientEmail = email;
        this.recipientPhone = phone;
    }

    public void setTemplateInfo(String templateName, String templateData) {
        this.templateName = templateName;
        this.templateData = templateData;
    }

    public void setExternalReference(String externalId) {
        this.externalId = externalId;
    }

    public long getProcessingTimeMinutes() {
        if (sentAt != null && createdAt != null) {
            return java.time.Duration.between(createdAt, sentAt).toMinutes();
        }
        return 0;
    }

    public boolean isDelayed() {
        if (status == NotificationStatus.PENDING && createdAt != null) {
            long minutesSinceCreation = java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
            return minutesSinceCreation > (isRealTime() ? 5 : 30);
        }
        return false;
    }
}