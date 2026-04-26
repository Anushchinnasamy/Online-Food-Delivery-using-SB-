package com.fooddelivery.notification.service;

import com.fooddelivery.notification.dto.request.BulkNotificationRequestDTO;
import com.fooddelivery.notification.dto.request.NotificationRequestDTO;
import com.fooddelivery.notification.dto.response.BulkNotificationResponseDTO;
import com.fooddelivery.notification.dto.response.NotificationResponseDTO;
import com.fooddelivery.notification.dto.response.NotificationSummaryResponseDTO;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.enums.NotificationType;
import com.fooddelivery.notification.exception.NotificationNotFoundException;
import com.fooddelivery.notification.mapper.NotificationMapper;
import com.fooddelivery.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class for notification operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSenderService notificationSenderService;

    /**
     * Send a single notification
     */
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        log.info("Sending notification to user: {} for event: {}", request.getUserId(), request.getEventType());

        // Create notification entity
        Notification notification = notificationMapper.toEntity(request);
        notification.setStatus(NotificationStatus.PENDING);

        // Save notification
        notification = notificationRepository.save(notification);

        // Send notification asynchronously
        sendNotificationAsync(notification);

        log.info("Notification created with ID: {}", notification.getId());
        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Send bulk notifications
     */
    public BulkNotificationResponseDTO sendBulkNotifications(BulkNotificationRequestDTO request) {
        log.info("Sending bulk notifications to {} users for event: {}", 
            request.getUserIds().size(), request.getEventType());

        List<Long> notificationIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successfullyCreated = 0;

        for (Long userId : request.getUserIds()) {
            try {
                // Create individual notification request
                NotificationRequestDTO individualRequest = new NotificationRequestDTO();
                individualRequest.setUserId(userId);
                individualRequest.setReferenceId(request.getReferenceId());
                individualRequest.setNotificationType(request.getNotificationType());
                individualRequest.setEventType(request.getEventType());
                individualRequest.setMessage(request.getMessage());
                individualRequest.setSubject(request.getSubject());
                individualRequest.setTemplateName(request.getTemplateName());
                individualRequest.setTemplateData(request.getTemplateData());
                individualRequest.setMaxRetryAttempts(request.getMaxRetryAttempts());
                individualRequest.setMetadata(request.getMetadata());

                // Create notification entity
                Notification notification = notificationMapper.toEntity(individualRequest);
                notification.setStatus(NotificationStatus.PENDING);

                // Save notification
                notification = notificationRepository.save(notification);
                notificationIds.add(notification.getId());
                successfullyCreated++;

                // Send notification asynchronously
                sendNotificationAsync(notification);

            } catch (Exception e) {
                log.error("Failed to create notification for user: {}", userId, e);
                errors.add("Failed to create notification for user " + userId + ": " + e.getMessage());
            }
        }

        log.info("Bulk notifications created: {} successful, {} failed", successfullyCreated, errors.size());
        return new BulkNotificationResponseDTO(
            request.getUserIds().size(),
            successfullyCreated,
            errors.size(),
            notificationIds,
            errors
        );
    }

    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        Notification notification = findNotificationById(id);
        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Get user's notifications with pagination
     */
    @Transactional(readOnly = true)
    public Page<NotificationSummaryResponseDTO> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(notificationMapper::toSummaryResponseDTO);
    }

    /**
     * Get user's recent notifications
     */
    @Transactional(readOnly = true)
    public List<NotificationSummaryResponseDTO> getUserRecentNotifications(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Notification> notifications = notificationRepository.findRecentNotificationsByUserId(userId, pageable);
        return notificationMapper.toSummaryResponseDTOList(notifications);
    }

    /**
     * Get all notifications with pagination
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(notificationMapper::toResponseDTO);
    }

    /**
     * Get notifications by status
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByStatus(NotificationStatus status) {
        List<Notification> notifications = notificationRepository.findByStatusOrderByCreatedAtDesc(status);
        return notificationMapper.toResponseDTOList(notifications);
    }

    /**
     * Get notifications by event type
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByEventType(EventType eventType) {
        List<Notification> notifications = notificationRepository.findByEventTypeOrderByCreatedAtDesc(eventType);
        return notificationMapper.toResponseDTOList(notifications);
    }

    /**
     * Get notifications by reference ID
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByReferenceId(Long referenceId) {
        List<Notification> notifications = notificationRepository.findByReferenceIdOrderByCreatedAtDesc(referenceId);
        return notificationMapper.toResponseDTOList(notifications);
    }

    /**
     * Retry failed notification
     */
    public NotificationResponseDTO retryNotification(Long id) {
        log.info("Retrying notification: {}", id);

        Notification notification = findNotificationById(id);

        if (!notification.canBeRetried()) {
            throw new IllegalStateException("Notification cannot be retried. Status: " + notification.getStatus() + 
                ", Retry count: " + notification.getRetryCount() + "/" + notification.getMaxRetryAttempts());
        }

        // Mark as retrying
        notification.markAsRetrying();
        notification = notificationRepository.save(notification);

        // Send notification asynchronously
        sendNotificationAsync(notification);

        log.info("Notification retry initiated: {}", id);
        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Process pending notifications
     */
    @Transactional
    public void processPendingNotifications() {
        log.info("Processing pending notifications");

        // Find pending notifications older than 5 minutes
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        List<Notification> pendingNotifications = notificationRepository.findPendingNotificationsOlderThan(cutoffTime);

        for (Notification notification : pendingNotifications) {
            try {
                sendNotificationAsync(notification);
                log.debug("Reprocessed pending notification: {}", notification.getId());
            } catch (Exception e) {
                log.error("Failed to reprocess pending notification: {}", notification.getId(), e);
            }
        }

        log.info("Processed {} pending notifications", pendingNotifications.size());
    }

    /**
     * Process retry notifications
     */
    @Transactional
    public void processRetryNotifications() {
        log.info("Processing retry notifications");

        LocalDateTime currentTime = LocalDateTime.now();
        List<Notification> retryNotifications = notificationRepository.findNotificationsReadyForRetry(currentTime);

        for (Notification notification : retryNotifications) {
            try {
                sendNotificationAsync(notification);
                log.debug("Retried notification: {}", notification.getId());
            } catch (Exception e) {
                log.error("Failed to retry notification: {}", notification.getId(), e);
                notification.markAsFailed("Retry failed: " + e.getMessage());
                notificationRepository.save(notification);
            }
        }

        log.info("Processed {} retry notifications", retryNotifications.size());
    }

    /**
     * Get notification statistics
     */
    @Transactional(readOnly = true)
    public NotificationStatsDTO getNotificationStats() {
        long totalNotifications = notificationRepository.count();
        long pendingNotifications = notificationRepository.countByStatus(NotificationStatus.PENDING);
        long sentNotifications = notificationRepository.countByStatus(NotificationStatus.SENT);
        long failedNotifications = notificationRepository.countByStatus(NotificationStatus.FAILED);
        long retryingNotifications = notificationRepository.countByStatus(NotificationStatus.RETRYING);

        Double avgProcessingTime = notificationRepository.getAverageProcessingTimeMinutes();

        return new NotificationStatsDTO(
            totalNotifications,
            pendingNotifications,
            sentNotifications,
            failedNotifications,
            retryingNotifications,
            avgProcessingTime != null ? avgProcessingTime : 0.0
        );
    }

    /**
     * Get delayed notifications
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getDelayedNotifications() {
        LocalDateTime realtimeCutoff = LocalDateTime.now().minusMinutes(5);
        LocalDateTime emailCutoff = LocalDateTime.now().minusMinutes(30);
        
        List<Notification> delayedNotifications = notificationRepository
            .findDelayedNotifications(realtimeCutoff, emailCutoff);
        
        return notificationMapper.toResponseDTOList(delayedNotifications);
    }

    // Private helper methods

    @Async
    private void sendNotificationAsync(Notification notification) {
        try {
            CompletableFuture<Void> future = notificationSenderService.sendNotification(notification);
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to send notification: {}", notification.getId(), throwable);
                    notification.markAsFailed(throwable.getMessage());
                    notificationRepository.save(notification);
                } else {
                    log.debug("Notification sent successfully: {}", notification.getId());
                }
            });
        } catch (Exception e) {
            log.error("Failed to initiate notification sending: {}", notification.getId(), e);
            notification.markAsFailed("Failed to initiate sending: " + e.getMessage());
            notificationRepository.save(notification);
        }
    }

    private Notification findNotificationById(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));
    }

    /**
     * DTO for notification statistics
     */
    public record NotificationStatsDTO(
        long totalNotifications,
        long pendingNotifications,
        long sentNotifications,
        long failedNotifications,
        long retryingNotifications,
        double averageProcessingTimeMinutes
    ) {}
}