package com.fooddelivery.menu.mapper;

import com.fooddelivery.menu.dto.request.MenuItemCreateRequestDTO;
import com.fooddelivery.menu.dto.request.MenuItemUpdateRequestDTO;
import com.fooddelivery.menu.dto.response.MenuItemDetailResponseDTO;
import com.fooddelivery.menu.dto.response.MenuItemResponseDTO;
import com.fooddelivery.menu.entity.MenuItem;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for MenuItem entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MenuItemMapper {

    /**
     * Convert MenuItemCreateRequestDTO to MenuItem entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    MenuItem toEntity(MenuItemCreateRequestDTO dto);

    /**
     * Update MenuItem entity from MenuItemUpdateRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "isVegetarian", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(MenuItemUpdateRequestDTO dto, @MappingTarget MenuItem menuItem);

    /**
     * Convert MenuItem entity to MenuItemResponseDTO
     */
    MenuItemResponseDTO toResponseDTO(MenuItem menuItem);

    /**
     * Convert MenuItem entity to MenuItemDetailResponseDTO (admin view)
     */
    MenuItemDetailResponseDTO toDetailResponseDTO(MenuItem menuItem);

    /**
     * Convert list of MenuItem entities to MenuItemResponseDTO list
     */
    List<MenuItemResponseDTO> toResponseDTOList(List<MenuItem> menuItems);

    /**
     * Convert list of MenuItem entities to MenuItemDetailResponseDTO list
     */
    List<MenuItemDetailResponseDTO> toDetailResponseDTOList(List<MenuItem> menuItems);
}