package com.fooddelivery.menu.service;

import com.fooddelivery.menu.client.RestaurantServiceClient;
import com.fooddelivery.menu.dto.request.MenuItemCreateRequestDTO;
import com.fooddelivery.menu.dto.request.MenuItemUpdateRequestDTO;
import com.fooddelivery.menu.dto.response.MenuItemDetailResponseDTO;
import com.fooddelivery.menu.dto.response.MenuItemResponseDTO;
import com.fooddelivery.menu.entity.MenuItem;
import com.fooddelivery.menu.exception.MenuItemAlreadyExistsException;
import com.fooddelivery.menu.exception.MenuItemNotFoundException;
import com.fooddelivery.menu.exception.UnauthorizedAccessException;
import com.fooddelivery.menu.mapper.MenuItemMapper;
import com.fooddelivery.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service class for menu item management operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;
    private final RestaurantServiceClient restaurantServiceClient;

    /**
     * Create a new menu item
     */
    public MenuItemDetailResponseDTO createMenuItem(MenuItemCreateRequestDTO request, Long currentUserRestaurantId) {
        log.info("Creating new menu item: {} for restaurant: {}", request.getName(), request.getRestaurantId());

        // Validate restaurant ownership
        validateRestaurantOwnership(request.getRestaurantId(), currentUserRestaurantId);

        // Validate restaurant exists (optional - can be skipped if service is down)
        validateRestaurantExists(request.getRestaurantId());

        // Check if menu item with same name already exists for this restaurant
        if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(request.getRestaurantId(), request.getName())) {
            throw new MenuItemAlreadyExistsException("Menu item with name '" + request.getName() + "' already exists for this restaurant");
        }

        MenuItem menuItem = menuItemMapper.toEntity(request);
        menuItem = menuItemRepository.save(menuItem);

        log.info("Menu item created successfully with ID: {}", menuItem.getId());
        return menuItemMapper.toDetailResponseDTO(menuItem);
    }

    /**
     * Update menu item details
     */
    public MenuItemDetailResponseDTO updateMenuItem(Long menuItemId, MenuItemUpdateRequestDTO request, Long currentUserRestaurantId) {
        log.info("Updating menu item with ID: {}", menuItemId);

        MenuItem menuItem = findMenuItemById(menuItemId);

        // Validate restaurant ownership
        validateRestaurantOwnership(menuItem.getRestaurantId(), currentUserRestaurantId);

        // Check if name is being updated and if it conflicts with existing items
        if (request.getName() != null && !request.getName().equalsIgnoreCase(menuItem.getName())) {
            if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCaseAndIdNot(
                    menuItem.getRestaurantId(), request.getName(), menuItemId)) {
                throw new MenuItemAlreadyExistsException("Menu item with name '" + request.getName() + "' already exists for this restaurant");
            }
        }

        menuItemMapper.updateEntityFromDTO(request, menuItem);
        menuItem = menuItemRepository.save(menuItem);

        log.info("Menu item updated successfully with ID: {}", menuItem.getId());
        return menuItemMapper.toDetailResponseDTO(menuItem);
    }

    /**
     * Update menu item availability
     */
    public MenuItemDetailResponseDTO updateMenuItemAvailability(Long menuItemId, Boolean isAvailable, Long currentUserRestaurantId) {
        log.info("Updating menu item availability for ID: {} to {}", menuItemId, isAvailable);

        MenuItem menuItem = findMenuItemById(menuItemId);

        // Validate restaurant ownership
        validateRestaurantOwnership(menuItem.getRestaurantId(), currentUserRestaurantId);

        if (isAvailable) {
            menuItem.makeAvailable();
        } else {
            menuItem.makeUnavailable();
        }

        menuItem = menuItemRepository.save(menuItem);

        log.info("Menu item availability updated successfully for ID: {}", menuItem.getId());
        return menuItemMapper.toDetailResponseDTO(menuItem);
    }

    /**
     * Soft delete menu item
     */
    public void deleteMenuItem(Long menuItemId, Long currentUserRestaurantId) {
        log.info("Deleting menu item with ID: {}", menuItemId);

        MenuItem menuItem = findMenuItemById(menuItemId);

        // Validate restaurant ownership
        validateRestaurantOwnership(menuItem.getRestaurantId(), currentUserRestaurantId);

        menuItem.softDelete();
        menuItemRepository.save(menuItem);

        log.info("Menu item deleted successfully with ID: {}", menuItem.getId());
    }

    /**
     * Get menu items by restaurant ID (customer view - only available items)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getMenuItemsByRestaurantId(Long restaurantId) {
        log.debug("Fetching available menu items for restaurant: {}", restaurantId);

        List<MenuItem> menuItems = menuItemRepository.findAvailableMenuItemsByRestaurantId(restaurantId);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Get menu items by restaurant ID with pagination (customer view)
     */
    @Transactional(readOnly = true)
    public Page<MenuItemResponseDTO> getMenuItemsByRestaurantId(Long restaurantId, Pageable pageable) {
        log.debug("Fetching available menu items for restaurant: {} with pagination", restaurantId);

        Page<MenuItem> menuItems = menuItemRepository.findAvailableMenuItemsByRestaurantId(restaurantId, pageable);
        return menuItems.map(menuItemMapper::toResponseDTO);
    }

    /**
     * Get all menu items by restaurant ID (admin view - including unavailable)
     */
    @Transactional(readOnly = true)
    public List<MenuItemDetailResponseDTO> getAllMenuItemsByRestaurantId(Long restaurantId, Long currentUserRestaurantId) {
        log.debug("Fetching all menu items for restaurant: {}", restaurantId);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        List<MenuItem> menuItems = menuItemRepository.findAllMenuItemsByRestaurantId(restaurantId);
        return menuItemMapper.toDetailResponseDTOList(menuItems);
    }

    /**
     * Get menu items by restaurant and category (customer view)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getMenuItemsByRestaurantIdAndCategory(Long restaurantId, String category) {
        log.debug("Fetching available menu items for restaurant: {} and category: {}", restaurantId, category);

        List<MenuItem> menuItems = menuItemRepository.findAvailableMenuItemsByRestaurantIdAndCategory(restaurantId, category);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Get vegetarian menu items by restaurant (customer view)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getVegetarianMenuItemsByRestaurantId(Long restaurantId) {
        log.debug("Fetching vegetarian menu items for restaurant: {}", restaurantId);

        List<MenuItem> menuItems = menuItemRepository.findAvailableVegetarianMenuItemsByRestaurantId(restaurantId);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Get non-vegetarian menu items by restaurant (customer view)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getNonVegetarianMenuItemsByRestaurantId(Long restaurantId) {
        log.debug("Fetching non-vegetarian menu items for restaurant: {}", restaurantId);

        List<MenuItem> menuItems = menuItemRepository.findAvailableNonVegetarianMenuItemsByRestaurantId(restaurantId);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Search menu items by name within a restaurant
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> searchMenuItemsByName(Long restaurantId, String name) {
        log.debug("Searching menu items by name: {} for restaurant: {}", name, restaurantId);

        List<MenuItem> menuItems = menuItemRepository.searchAvailableMenuItemsByRestaurantIdAndName(restaurantId, name);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Get distinct categories for a restaurant
     */
    @Transactional(readOnly = true)
    public List<String> getCategoriesByRestaurantId(Long restaurantId) {
        log.debug("Fetching categories for restaurant: {}", restaurantId);

        return menuItemRepository.findDistinctCategoriesByRestaurantId(restaurantId);
    }

    /**
     * Get menu item by ID (admin view)
     */
    @Transactional(readOnly = true)
    public MenuItemDetailResponseDTO getMenuItemById(Long menuItemId, Long currentUserRestaurantId) {
        log.debug("Fetching menu item details for ID: {}", menuItemId);

        MenuItem menuItem = findMenuItemById(menuItemId);

        // Validate restaurant ownership
        validateRestaurantOwnership(menuItem.getRestaurantId(), currentUserRestaurantId);

        return menuItemMapper.toDetailResponseDTO(menuItem);
    }

    /**
     * Bulk update availability for restaurant menu items
     */
    public void updateRestaurantMenuAvailability(Long restaurantId, Boolean isAvailable, Long currentUserRestaurantId) {
        log.info("Bulk updating menu availability for restaurant: {} to {}", restaurantId, isAvailable);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        menuItemRepository.updateAvailabilityByRestaurantId(restaurantId, isAvailable);

        log.info("Bulk menu availability update completed for restaurant: {}", restaurantId);
    }

    /**
     * Bulk update availability for menu items by category
     */
    public void updateCategoryMenuAvailability(Long restaurantId, String category, Boolean isAvailable, Long currentUserRestaurantId) {
        log.info("Bulk updating menu availability for restaurant: {} category: {} to {}", restaurantId, category, isAvailable);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        menuItemRepository.updateAvailabilityByRestaurantIdAndCategory(restaurantId, category, isAvailable);

        log.info("Bulk category menu availability update completed for restaurant: {} category: {}", restaurantId, category);
    }

    /**
     * Get menu items by IDs (for order validation)
     */
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getMenuItemsByIds(List<Long> menuItemIds) {
        log.debug("Fetching menu items by IDs: {}", menuItemIds);

        List<MenuItem> menuItems = menuItemRepository.findAvailableMenuItemsByIds(menuItemIds);
        return menuItemMapper.toResponseDTOList(menuItems);
    }

    /**
     * Helper method to find menu item by ID
     */
    private MenuItem findMenuItemById(Long menuItemId) {
        return menuItemRepository.findByIdAndNotDeleted(menuItemId)
            .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with ID: " + menuItemId));
    }

    /**
     * Validate restaurant ownership
     */
    private void validateRestaurantOwnership(Long restaurantId, Long currentUserRestaurantId) {
        if (currentUserRestaurantId == null) {
            throw new UnauthorizedAccessException("Restaurant ID not found in request context");
        }
        
        if (!restaurantId.equals(currentUserRestaurantId)) {
            throw new UnauthorizedAccessException("You can only manage menu items for your own restaurant");
        }
    }

    /**
     * Validate restaurant exists (optional validation)
     */
    private void validateRestaurantExists(Long restaurantId) {
        try {
            ResponseEntity<Map<String, Object>> response = restaurantServiceClient.getRestaurantDetails(restaurantId);
            if (response.getBody() == null) {
                log.warn("Restaurant service unavailable, skipping restaurant validation for ID: {}", restaurantId);
                return;
            }
            
            Map<String, Object> restaurant = response.getBody();
            Boolean isActive = (Boolean) restaurant.get("is_active");
            Boolean isApproved = (Boolean) restaurant.get("is_approved");
            
            if (isActive == null || isApproved == null || !isActive || !isApproved) {
                throw new IllegalStateException("Restaurant is not active or approved");
            }
            
        } catch (Exception e) {
            log.warn("Failed to validate restaurant existence for ID: {}, proceeding anyway: {}", restaurantId, e.getMessage());
            // Continue without validation if restaurant service is unavailable
        }
    }
}