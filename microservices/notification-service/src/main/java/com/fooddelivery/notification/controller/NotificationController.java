package com.fooddelivery.notification.controller;

import com.fooddelivery.notification.dto.request.BulkNotificationRequestDTO;
import com.fooddelivery.notification.dto.request.NotificationRequestDTO;
import com.fooddelivery.notification.dto.response.BulkNotificationResponseDTO;
import com.fooddelivery.notification.dto.response.NotificationResponseDTO;
import com.fooddelivery.notification.dto.response.NotificationSummaryResponseDTO;
import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for notification operations
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send a single notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        log.info("Sending notification to user: {} for event: {}", request.getUserId(), request.getEventType());
        NotificationResponseDTO response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send bulk notifications
     */
    @PostMapping("/bulk")
    public ResponseEntity<BulkNotificationResponseDTO> sendBulkNotifications(@Valid @RequestBody BulkNotificationRequestDTO request) {
        log.info("Sending bulk notifications to {} users for event: {}", request.getUserIds().size(), request.getEventType());
        BulkNotificationResponseDTO response = notificationService.sendBulkNotifications(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotification(@PathVariable Long id) {
        log.info("Getting notification: {}", id);
        NotificationResponseDTO response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's notifications with pagination
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationSummaryResponseDTO>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting notifications for user: {} (page: {}, size: {})", userId, page, size);
        Page<NotificationSummaryResponseDTO> response = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's recent notifications
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<NotificationSummaryResponseDTO>> getUserRecentNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting recent notifications for user: {} (limit: {})", userId, limit);
        List<NotificationSummaryResponseDTO> response = notificationService.getUserRecentNotifications(userId, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all notifications with pagination (admin endpoint)
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponseDTO>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting all notifications (page: {}, size: {})", page, size);
        Page<NotificationResponseDTO> response = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by status (admin endpoint)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        log.info("Getting notifications by status: {}", status);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by event type (admin endpoint)
     */
    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByEventType(@PathVariable EventType eventType) {
        log.info("Getting notifications by event type: {}", eventType);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByEventType(eventType);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by reference ID
     */
    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByReferenceId(@PathVariable Long referenceId) {
        log.info("Getting notifications by reference ID: {}", referenceId);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByReferenceId(referenceId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retry failed notification (admin endpoint)
     */
    @PutMapping("/{id}/retry")
    public ResponseEntity<NotificationResponseDTO> retryNotification(@PathVariable Long id) {
        log.info("Retrying notification: {}", id);
        NotificationResponseDTO response = notificationService.retryNotification(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notification statistics (admin endpoint)
     */
    @GetMapping("/stats")
    public ResponseEntity<NotificationService.NotificationStatsDTO> getNotificationStats() {
        log.info("Getting notification statistics");
        NotificationService.NotificationStatsDTO stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get delayed notifications (admin endpoint)
     */
    @GetMapping("/delayed")
    public ResponseEntity<List<NotificationResponseDTO>> getDelayedNotifications() {
        log.info("Getting delayed notifications");
        List<NotificationResponseDTO> response = notificationService.getDelayedNotifications();
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is healthy");
    }
}