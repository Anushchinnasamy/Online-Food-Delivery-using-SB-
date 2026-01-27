package com.fooddelivery.notification.service;

import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.enums.NotificationType;
import com.fooddelivery.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending notifications through different channels
 * This is a simulation service for demo purposes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSenderService {

    private final NotificationRepository notificationRepository;
    private final Random random = new Random();

    /**
     * Send notification through appropriate channel
     */
    @Async
    public CompletableFuture<Void> sendNotification(Notification notification) {
        log.info("Sending {} notification: {} to user: {}", 
            notification.getNotificationType(), notification.getId(), notification.getUserId());

        return CompletableFuture.runAsync(() -> {
            try {
                // Simulate processing delay based on notification type
                simulateProcessingDelay(notification.getNotificationType());

                // Send notification based on type
                switch (notification.getNotificationType()) {
                    case EMAIL -> sendEmailNotification(notification);
                    case SMS -> sendSmsNotification(notification);
                    case PUSH -> sendPushNotification(notification);
                }

                // Mark as sent
                notification.markAsSent();
                notificationRepository.save(notification);

                log.info("Notification sent successfully: {}", notification.getId());

            } catch (Exception e) {
                log.error("Failed to send notification: {}", notification.getId(), e);
                
                // Mark as failed or retry
                if (notification.canBeRetried()) {
                    notification.markAsRetrying();
                } else {
                    notification.markAsFailed(e.getMessage());
                }
                notificationRepository.save(notification);
                
                throw new RuntimeException("Notification sending failed", e);
            }
        });
    }

    /**
     * Send email notification (simulated)
     */
    private void sendEmailNotification(Notification notification) throws Exception {
        log.debug("Sending email notification: {}", notification.getId());

        // Simulate email sending with 95% success rate
        if (random.nextDouble() < 0.05) {
            throw new Exception(getRandomEmailFailureReason());
        }

        // Simulate external service response
        String externalId = "EMAIL_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
        notification.setExternalReference(externalId);

        log.debug("Email sent successfully: {} with external ID: {}", notification.getId(), externalId);
    }

    /**
     * Send SMS notification (simulated)
     */
    private void sendSmsNotification(Notification notification) throws Exception {
        log.debug("Sending SMS notification: {}", notification.getId());

        // Simulate SMS sending with 92% success rate
        if (random.nextDouble() < 0.08) {
            throw new Exception(getRandomSmsFailureReason());
        }

        // Simulate external service response
        String externalId = "SMS_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
        notification.setExternalReference(externalId);

        log.debug("SMS sent successfully: {} with external ID: {}", notification.getId(), externalId);
    }

    /**
     * Send push notification (simulated)
     */
    private void sendPushNotification(Notification notification) throws Exception {
        log.debug("Sending push notification: {}", notification.getId());

        // Simulate push notification with 98% success rate
        if (random.nextDouble() < 0.02) {
            throw new Exception(getRandomPushFailureReason());
        }

        // Simulate external service response
        String externalId = "PUSH_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
        notification.setExternalReference(externalId);

        log.debug("Push notification sent successfully: {} with external ID: {}", notification.getId(), externalId);
    }

    /**
     * Simulate processing delay based on notification type
     */
    private void simulateProcessingDelay(NotificationType type) throws InterruptedException {
        int delayMs = switch (type) {
            case PUSH -> 100 + random.nextInt(200); // 100-300ms
            case SMS -> 500 + random.nextInt(1000); // 500-1500ms
            case EMAIL -> 1000 + random.nextInt(2000); // 1-3 seconds
        };
        
        Thread.sleep(delayMs);
    }

    /**
     * Get random email failure reason
     */
    private String getRandomEmailFailureReason() {
        String[] reasons = {
            "Invalid email address",
            "Email server timeout",
            "Recipient mailbox full",
            "Email blocked by spam filter",
            "SMTP server error",
            "Network connection failed"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    /**
     * Get random SMS failure reason
     */
    private String getRandomSmsFailureReason() {
        String[] reasons = {
            "Invalid phone number",
            "SMS gateway timeout",
            "Insufficient SMS credits",
            "Phone number blocked",
            "Network operator error",
            "Message content rejected"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    /**
     * Get random push notification failure reason
     */
    private String getRandomPushFailureReason() {
        String[] reasons = {
            "Device token invalid",
            "Push service unavailable",
            "App not installed",
            "Notifications disabled",
            "Network error",
            "Payload too large"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    /**
     * Validate notification before sending
     */
    private void validateNotification(Notification notification) throws Exception {
        if (notification.getNotificationType() == NotificationType.EMAIL) {
            if (notification.getRecipientEmail() == null || notification.getRecipientEmail().trim().isEmpty()) {
                throw new Exception("Email address is required for email notifications");
            }
        }

        if (notification.getNotificationType() == NotificationType.SMS) {
            if (notification.getRecipientPhone() == null || notification.getRecipientPhone().trim().isEmpty()) {
                throw new Exception("Phone number is required for SMS notifications");
            }
        }

        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new Exception("Message content is required");
        }
    }
}