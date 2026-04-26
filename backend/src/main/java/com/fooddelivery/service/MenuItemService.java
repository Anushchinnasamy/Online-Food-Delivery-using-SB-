package com.fooddelivery.service;

import com.fooddelivery.dto.MenuItemDTO;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.exception.UnauthorizedException;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for menu item operations
 */
@Service
@Transactional
public class MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Get all menu items for a restaurant
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItemsByRestaurant(Long restaurantId) {
        logger.info("Fetching menu items for restaurant ID: {}", restaurantId);
        return menuItemRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available menu items for a restaurant
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getAvailableMenuItems(Long restaurantId) {
        logger.info("Fetching available menu items for restaurant ID: {}", restaurantId);
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrueAndDeletedAtIsNull(restaurantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get menu item by ID
     */
    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItemById(Long id) {
        logger.info("Fetching menu item with ID: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        return convertToDTO(menuItem);
    }

    /**
     * Search menu items by name
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> searchByName(String name) {
        logger.info("Searching menu items with name containing: {}", name);
        return menuItemRepository.findByNameContainingIgnoreCaseAndIsAvailableTrueAndDeletedAtIsNull(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get menu items by category
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getByCategory(Long restaurantId, String category) {
        logger.info("Fetching menu items for restaurant {} in category: {}", restaurantId, category);
        return menuItemRepository.findByRestaurantIdAndCategoryAndIsAvailableTrueAndDeletedAtIsNull(restaurantId, category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get vegetarian menu items
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getVegetarianItems(Long restaurantId) {
        logger.info("Fetching vegetarian menu items for restaurant ID: {}", restaurantId);
        return menuItemRepository.findByRestaurantIdAndIsVegetarianTrueAndIsAvailableTrueAndDeletedAtIsNull(restaurantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create menu item
     */
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO, Long userId) {
        logger.info("Creating new menu item: {}", menuItemDTO.getName());

        Restaurant restaurant = restaurantRepository.findById(menuItemDTO.getRestaurantId())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", menuItemDTO.getRestaurantId()));

        // Check if user is the owner
        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to add menu items to this restaurant");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setCategory(menuItemDTO.getCategory());
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setIsVegetarian(menuItemDTO.getIsVegetarian());
        menuItem.setIsVegan(menuItemDTO.getIsVegan());
        menuItem.setIsSpicy(menuItemDTO.getIsSpicy());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        menuItem.setPreparationTimeMinutes(menuItemDTO.getPreparationTimeMinutes());
        menuItem.setIngredients(menuItemDTO.getIngredients());
        menuItem.setCaloriesPerServing(menuItemDTO.getCaloriesPerServing());
        menuItem.setServingSize(menuItemDTO.getServingSize());
        menuItem.setRestaurant(restaurant);

        menuItem = menuItemRepository.save(menuItem);
        logger.info("Menu item created successfully with ID: {}", menuItem.getId());

        return convertToDTO(menuItem);
    }

    /**
     * Update menu item
     */
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO, Long userId) {
        logger.info("Updating menu item with ID: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        // Check if user is the owner
        if (!menuItem.getRestaurant().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this menu item");
        }

        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setCategory(menuItemDTO.getCategory());
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setIsVegetarian(menuItemDTO.getIsVegetarian());
        menuItem.setIsVegan(menuItemDTO.getIsVegan());
        menuItem.setIsSpicy(menuItemDTO.getIsSpicy());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        menuItem.setPreparationTimeMinutes(menuItemDTO.getPreparationTimeMinutes());
        menuItem.setIngredients(menuItemDTO.getIngredients());
        menuItem.setCaloriesPerServing(menuItemDTO.getCaloriesPerServing());
        menuItem.setServingSize(menuItemDTO.getServingSize());

        menuItem = menuItemRepository.save(menuItem);
        logger.info("Menu item updated successfully");

        return convertToDTO(menuItem);
    }

    /**
     * Delete menu item (soft delete)
     */
    public void deleteMenuItem(Long id, Long userId) {
        logger.info("Deleting menu item with ID: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        // Check if user is the owner
        if (!menuItem.getRestaurant().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this menu item");
        }

        menuItem.softDelete();
        menuItemRepository.save(menuItem);
        logger.info("Menu item deleted successfully");
    }

    /**
     * Toggle menu item availability
     */
    public MenuItemDTO toggleAvailability(Long id, Long userId) {
        logger.info("Toggling availability for menu item ID: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        // Check if user is the owner
        if (!menuItem.getRestaurant().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this menu item");
        }

        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        menuItem = menuItemRepository.save(menuItem);
        logger.info("Menu item availability toggled to: {}", menuItem.getIsAvailable());

        return convertToDTO(menuItem);
    }

    /**
     * Convert MenuItem entity to DTO
     */
    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setCategory(menuItem.getCategory());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setIsVegetarian(menuItem.getIsVegetarian());
        dto.setIsVegan(menuItem.getIsVegan());
        dto.setIsSpicy(menuItem.getIsSpicy());
        dto.setIsAvailable(menuItem.getIsAvailable());
        dto.setPreparationTimeMinutes(menuItem.getPreparationTimeMinutes());
        dto.setIngredients(menuItem.getIngredients());
        dto.setCaloriesPerServing(menuItem.getCaloriesPerServing());
        dto.setServingSize(menuItem.getServingSize());
        dto.setRestaurantId(menuItem.getRestaurant().getId());
        dto.setRestaurantName(menuItem.getRestaurant().getName());
        dto.setCreatedAt(menuItem.getCreatedAt());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }
}
