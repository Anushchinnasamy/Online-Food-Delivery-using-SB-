package com.fooddelivery.notification.mapper;

import com.fooddelivery.notification.dto.request.NotificationRequestDTO;
import com.fooddelivery.notification.dto.response.NotificationResponseDTO;
import com.fooddelivery.notification.dto.response.NotificationSummaryResponseDTO;
import com.fooddelivery.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Notification entity and DTOs
 * Using manual mapping instead of MapStruct for simplicity
 */
@Component
public class NotificationMapper {

    /**
     * Convert NotificationRequestDTO to Notification entity
     */
    public Notification toEntity(NotificationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setReferenceId(dto.getReferenceId());
        notification.setNotificationType(dto.getNotificationType());
        notification.setEventType(dto.getEventType());
        notification.setMessage(dto.getMessage());
        notification.setRecipientEmail(dto.getRecipientEmail());
        notification.setRecipientPhone(dto.getRecipientPhone());
        notification.setSubject(dto.getSubject());
        notification.setTemplateName(dto.getTemplateName());
        notification.setTemplateData(dto.getTemplateData());
        notification.setMaxRetryAttempts(dto.getMaxRetryAttempts());
        notification.setMetadata(dto.getMetadata());

        return notification;
    }

    /**
     * Convert Notification entity to NotificationResponseDTO
     */
    public NotificationResponseDTO toResponseDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setReferenceId(notification.getReferenceId());
        dto.setNotificationType(notification.getNotificationType());
        dto.setEventType(notification.getEventType());
        dto.setMessage(notification.getMessage());
        dto.setStatus(notification.getStatus());
        dto.setRecipientEmail(notification.getRecipientEmail());
        dto.setRecipientPhone(notification.getRecipientPhone());
        dto.setSubject(notification.getSubject());
        dto.setRetryCount(notification.getRetryCount());
        dto.setMaxRetryAttempts(notification.getMaxRetryAttempts());
        dto.setNextRetryAt(notification.getNextRetryAt());
        dto.setSentAt(notification.getSentAt());
        dto.setFailedAt(notification.getFailedAt());
        dto.setFailureReason(notification.getFailureReason());
        dto.setExternalId(notification.getExternalId());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());

        return dto;
    }

    /**
     * Convert Notification entity to NotificationSummaryResponseDTO
     */
    public NotificationSummaryResponseDTO toSummaryResponseDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationSummaryResponseDTO dto = new NotificationSummaryResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setReferenceId(notification.getReferenceId());
        dto.setNotificationType(notification.getNotificationType());
        dto.setEventType(notification.getEventType());
        dto.setMessage(notification.getMessage());
        dto.setStatus(notification.getStatus());
        dto.setSentAt(notification.getSentAt());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }

    /**
     * Convert list of Notification entities to list of NotificationResponseDTOs
     */
    public List<NotificationResponseDTO> toResponseDTOList(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }

        return notifications.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of Notification entities to list of NotificationSummaryResponseDTOs
     */
    public List<NotificationSummaryResponseDTO> toSummaryResponseDTOList(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }

        return notifications.stream()
                .map(this::toSummaryResponseDTO)
                .collect(Collectors.toList());
    }
}