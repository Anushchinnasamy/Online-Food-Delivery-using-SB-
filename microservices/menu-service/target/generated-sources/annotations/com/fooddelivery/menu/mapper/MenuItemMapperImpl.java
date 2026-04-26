package com.fooddelivery.menu.mapper;

import com.fooddelivery.menu.dto.request.MenuItemCreateRequestDTO;
import com.fooddelivery.menu.dto.request.MenuItemUpdateRequestDTO;
import com.fooddelivery.menu.dto.response.MenuItemDetailResponseDTO;
import com.fooddelivery.menu.dto.response.MenuItemResponseDTO;
import com.fooddelivery.menu.entity.MenuItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-27T21:02:23+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class MenuItemMapperImpl implements MenuItemMapper {

    @Override
    public MenuItem toEntity(MenuItemCreateRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MenuItem menuItem = new MenuItem();

        menuItem.setRestaurantId( dto.getRestaurantId() );
        menuItem.setName( dto.getName() );
        menuItem.setDescription( dto.getDescription() );
        menuItem.setPrice( dto.getPrice() );
        menuItem.setCategory( dto.getCategory() );
        menuItem.setImageUrl( dto.getImageUrl() );
        menuItem.setIsVegetarian( dto.getIsVegetarian() );

        menuItem.setIsAvailable( true );

        return menuItem;
    }

    @Override
    public void updateEntityFromDTO(MenuItemUpdateRequestDTO dto, MenuItem menuItem) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            menuItem.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            menuItem.setDescription( dto.getDescription() );
        }
        if ( dto.getPrice() != null ) {
            menuItem.setPrice( dto.getPrice() );
        }
        if ( dto.getCategory() != null ) {
            menuItem.setCategory( dto.getCategory() );
        }
        if ( dto.getImageUrl() != null ) {
            menuItem.setImageUrl( dto.getImageUrl() );
        }
        if ( dto.getIsAvailable() != null ) {
            menuItem.setIsAvailable( dto.getIsAvailable() );
        }
    }

    @Override
    public MenuItemResponseDTO toResponseDTO(MenuItem menuItem) {
        if ( menuItem == null ) {
            return null;
        }

        MenuItemResponseDTO menuItemResponseDTO = new MenuItemResponseDTO();

        menuItemResponseDTO.setId( menuItem.getId() );
        menuItemResponseDTO.setRestaurantId( menuItem.getRestaurantId() );
        menuItemResponseDTO.setName( menuItem.getName() );
        menuItemResponseDTO.setDescription( menuItem.getDescription() );
        menuItemResponseDTO.setPrice( menuItem.getPrice() );
        menuItemResponseDTO.setCategory( menuItem.getCategory() );
        menuItemResponseDTO.setImageUrl( menuItem.getImageUrl() );
        menuItemResponseDTO.setIsAvailable( menuItem.getIsAvailable() );
        menuItemResponseDTO.setIsVegetarian( menuItem.getIsVegetarian() );

        return menuItemResponseDTO;
    }

    @Override
    public MenuItemDetailResponseDTO toDetailResponseDTO(MenuItem menuItem) {
        if ( menuItem == null ) {
            return null;
        }

        MenuItemDetailResponseDTO menuItemDetailResponseDTO = new MenuItemDetailResponseDTO();

        menuItemDetailResponseDTO.setId( menuItem.getId() );
        menuItemDetailResponseDTO.setRestaurantId( menuItem.getRestaurantId() );
        menuItemDetailResponseDTO.setName( menuItem.getName() );
        menuItemDetailResponseDTO.setDescription( menuItem.getDescription() );
        menuItemDetailResponseDTO.setPrice( menuItem.getPrice() );
        menuItemDetailResponseDTO.setCategory( menuItem.getCategory() );
        menuItemDetailResponseDTO.setImageUrl( menuItem.getImageUrl() );
        menuItemDetailResponseDTO.setIsAvailable( menuItem.getIsAvailable() );
        menuItemDetailResponseDTO.setIsVegetarian( menuItem.getIsVegetarian() );
        menuItemDetailResponseDTO.setCreatedAt( menuItem.getCreatedAt() );
        menuItemDetailResponseDTO.setUpdatedAt( menuItem.getUpdatedAt() );

        return menuItemDetailResponseDTO;
    }

    @Override
    public List<MenuItemResponseDTO> toResponseDTOList(List<MenuItem> menuItems) {
        if ( menuItems == null ) {
            return null;
        }

        List<MenuItemResponseDTO> list = new ArrayList<MenuItemResponseDTO>( menuItems.size() );
        for ( MenuItem menuItem : menuItems ) {
            list.add( toResponseDTO( menuItem ) );
        }

        return list;
    }

    @Override
    public List<MenuItemDetailResponseDTO> toDetailResponseDTOList(List<MenuItem> menuItems) {
        if ( menuItems == null ) {
            return null;
        }

        List<MenuItemDetailResponseDTO> list = new ArrayList<MenuItemDetailResponseDTO>( menuItems.size() );
        for ( MenuItem menuItem : menuItems ) {
            list.add( toDetailResponseDTO( menuItem ) );
        }

        return list;
    }
}
