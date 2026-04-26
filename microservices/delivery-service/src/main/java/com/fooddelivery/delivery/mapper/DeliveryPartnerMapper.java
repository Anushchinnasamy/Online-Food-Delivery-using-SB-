package com.fooddelivery.delivery.mapper;

import com.fooddelivery.delivery.dto.request.DeliveryPartnerCreateRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryPartnerUpdateRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryPartnerResponseDTO;
import com.fooddelivery.delivery.entity.DeliveryPartner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for DeliveryPartner entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeliveryPartnerMapper {

    /**
     * Convert DeliveryPartnerCreateRequestDTO to DeliveryPartner entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "currentLatitude", ignore = true)
    @Mapping(target = "currentLongitude", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "totalDeliveries", ignore = true)
    @Mapping(target = "successfulDeliveries", ignore = true)
    @Mapping(target = "lastActiveAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DeliveryPartner toEntity(DeliveryPartnerCreateRequestDTO dto);

    /**
     * Convert DeliveryPartner entity to DeliveryPartnerResponseDTO
     */
    DeliveryPartnerResponseDTO toResponseDTO(DeliveryPartner deliveryPartner);

    /**
     * Convert list of DeliveryPartner entities to list of DeliveryPartnerResponseDTOs
     */
    List<DeliveryPartnerResponseDTO> toResponseDTOList(List<DeliveryPartner> deliveryPartners);

    /**
     * Update DeliveryPartner entity from DeliveryPartnerUpdateRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "totalDeliveries", ignore = true)
    @Mapping(target = "successfulDeliveries", ignore = true)
    @Mapping(target = "lastActiveAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(DeliveryPartnerUpdateRequestDTO dto, @MappingTarget DeliveryPartner deliveryPartner);
}