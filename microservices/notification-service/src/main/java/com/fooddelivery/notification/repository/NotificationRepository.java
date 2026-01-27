package com.fooddelivery.notification.repository;

import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by user ID with pagination
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find notifications by user ID and status
     */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);

    /**
     * Find notifications by reference ID
     */
    List<Notification> findByReferenceIdOrderByCreatedAtDesc(Long referenceId);

    /**
     * Find notifications by status
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);

    /**
     * Find notifications by notification type
     */
    List<Notification> findByNotificationTypeOrderByCreatedAtDesc(NotificationType notificationType);

    /**
     * Find notifications by event type
     */
    List<Notification> findByEventTypeOrderByCreatedAtDesc(EventType eventType);

    /**
     * Find notifications by status and created date range
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByStatusAndDateRange(
        @Param("status") NotificationStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find pending notifications older than specified time
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.createdAt < :cutoffTime ORDER BY n.createdAt ASC")
    List<Notification> findPendingNotificationsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find notifications ready for retry
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'RETRYING' AND n.nextRetryAt <= :currentTime ORDER BY n.nextRetryAt ASC")
    List<Notification> findNotificationsReadyForRetry(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find failed notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetryAttempts ORDER BY n.failedAt ASC")
    List<Notification> findFailedNotificationsForRetry();

    /**
     * Count notifications by status
     */
    long countByStatus(NotificationStatus status);

    /**
     * Count notifications by user and status
     */
    long countByUserIdAndStatus(Long userId, NotificationStatus status);

    /**
     * Count notifications by event type and status
     */
    long countByEventTypeAndStatus(EventType eventType, NotificationStatus status);

    /**
     * Find notifications by multiple statuses
     */
    @Query("SELECT n FROM Notification n WHERE n.status IN :statuses ORDER BY n.createdAt DESC")
    List<Notification> findByStatusIn(@Param("statuses") List<NotificationStatus> statuses);

    /**
     * Find recent notifications for user
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find notifications by user and event type
     */
    List<Notification> findByUserIdAndEventTypeOrderByCreatedAtDesc(Long userId, EventType eventType);

    /**
     * Find notifications by user and notification type
     */
    List<Notification> findByUserIdAndNotificationTypeOrderByCreatedAtDesc(Long userId, NotificationType notificationType);

    /**
     * Find delayed notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND " +
           "((n.notificationType IN ('SMS', 'PUSH') AND n.createdAt < :realtimeCutoff) OR " +
           "(n.notificationType = 'EMAIL' AND n.createdAt < :emailCutoff))")
    List<Notification> findDelayedNotifications(
        @Param("realtimeCutoff") LocalDateTime realtimeCutoff,
        @Param("emailCutoff") LocalDateTime emailCutoff
    );

    /**
     * Find notifications with external ID
     */
    List<Notification> findByExternalIdOrderByCreatedAtDesc(String externalId);

    /**
     * Count notifications by date range
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get notification statistics by status
     */
    @Query("SELECT n.status, COUNT(n) FROM Notification n GROUP BY n.status")
    List<Object[]> getNotificationStatsByStatus();

    /**
     * Get notification statistics by type
     */
    @Query("SELECT n.notificationType, COUNT(n) FROM Notification n GROUP BY n.notificationType")
    List<Object[]> getNotificationStatsByType();

    /**
     * Get notification statistics by event type
     */
    @Query("SELECT n.eventType, COUNT(n) FROM Notification n GROUP BY n.eventType")
    List<Object[]> getNotificationStatsByEventType();

    /**
     * Find notifications that exceeded max retries
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount >= n.maxRetryAttempts")
    List<Notification> findNotificationsThatExceededMaxRetries();

    /**
     * Calculate average processing time for sent notifications
     */
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (n.sentAt - n.createdAt))/60) FROM Notification n WHERE n.status = 'SENT' AND n.sentAt IS NOT NULL")
    Double getAverageProcessingTimeMinutes();

    /**
     * Find notifications by template name
     */
    List<Notification> findByTemplateNameOrderByCreatedAtDesc(String templateName);
}