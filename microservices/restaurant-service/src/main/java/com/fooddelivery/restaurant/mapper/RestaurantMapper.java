package com.fooddelivery.restaurant.mapper;

import com.fooddelivery.restaurant.dto.request.RestaurantCreateRequestDTO;
import com.fooddelivery.restaurant.dto.request.RestaurantUpdateRequestDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantDetailResponseDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantResponseDTO;
import com.fooddelivery.restaurant.entity.Restaurant;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Restaurant entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RestaurantMapper {

    /**
     * Convert RestaurantCreateRequestDTO to Restaurant entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", constant = "0.0")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isApproved", constant = "false")
    @Mapping(target = "isOpen", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Restaurant toEntity(RestaurantCreateRequestDTO dto);

    /**
     * Update Restaurant entity from RestaurantUpdateRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "pincode", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isApproved", ignore = true)
    @Mapping(target = "deliveryTimeMinutes", ignore = true)
    @Mapping(target = "minimumOrderAmount", ignore = true)
    @Mapping(target = "deliveryFee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(RestaurantUpdateRequestDTO dto, @MappingTarget Restaurant restaurant);

    /**
     * Convert Restaurant entity to RestaurantResponseDTO (customer view)
     */
    @Mapping(target = "isOpen", expression = "java(restaurant.isCurrentlyOpen())")
    RestaurantResponseDTO toResponseDTO(Restaurant restaurant);

    /**
     * Convert Restaurant entity to RestaurantDetailResponseDTO (admin view)
     */
    RestaurantDetailResponseDTO toDetailResponseDTO(Restaurant restaurant);

    /**
     * Convert list of Restaurant entities to RestaurantResponseDTO list
     */
    List<RestaurantResponseDTO> toResponseDTOList(List<Restaurant> restaurants);

    /**
     * Convert list of Restaurant entities to RestaurantDetailResponseDTO list
     */
    List<RestaurantDetailResponseDTO> toDetailResponseDTOList(List<Restaurant> restaurants);
}