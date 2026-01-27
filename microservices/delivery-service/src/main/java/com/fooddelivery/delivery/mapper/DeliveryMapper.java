package com.fooddelivery.delivery.mapper;

import com.fooddelivery.delivery.dto.request.DeliveryAssignRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryResponseDTO;
import com.fooddelivery.delivery.dto.response.DeliverySummaryResponseDTO;
import com.fooddelivery.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Delivery entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeliveryMapper {

    /**
     * Convert DeliveryAssignRequestDTO to Delivery entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deliveryPartnerId", ignore = true)
    @Mapping(target = "deliveryStatus", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "acceptedAt", ignore = true)
    @Mapping(target = "pickedUpAt", ignore = true)
    @Mapping(target = "onTheWayAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "estimatedDeliveryTime", ignore = true)
    @Mapping(target = "actualDeliveryTimeMinutes", ignore = true)
    @Mapping(target = "rejectionCount", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "deliveryNotes", ignore = true)
    @Mapping(target = "customerRating", ignore = true)
    @Mapping(target = "customerFeedback", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Delivery toEntity(DeliveryAssignRequestDTO dto);

    /**
     * Convert Delivery entity to DeliveryResponseDTO
     */
    DeliveryResponseDTO toResponseDTO(Delivery delivery);

    /**
     * Convert Delivery entity to DeliverySummaryResponseDTO
     */
    @Mapping(target = "deliveryPartnerName", ignore = true)
    @Mapping(target = "deliveryPartnerPhone", ignore = true)
    DeliverySummaryResponseDTO toSummaryResponseDTO(Delivery delivery);

    /**
     * Convert list of Delivery entities to list of DeliveryResponseDTOs
     */
    List<DeliveryResponseDTO> toResponseDTOList(List<Delivery> deliveries);

    /**
     * Convert list of Delivery entities to list of DeliverySummaryResponseDTOs
     */
    List<DeliverySummaryResponseDTO> toSummaryResponseDTOList(List<Delivery> deliveries);
}