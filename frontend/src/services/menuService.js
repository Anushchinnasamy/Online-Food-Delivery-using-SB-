import api from '../config/api';

const menuService = {
  // Get menu item by ID
  getMenuItemById: async (id) => {
    const response = await api.get(`/menu-items/${id}`);
    return response.data;
  },

  // Get menu items by restaurant
  getMenuItemsByRestaurant: async (restaurantId) => {
    const response = await api.get(`/menu-items/restaurant/${restaurantId}`);
    return response.data;
  },

  // Search menu items
  searchMenuItems: async (params) => {
    const response = await api.get('/menu-items/search', { params });
    return response.data;
  },

  // Create menu item
  createMenuItem: async (menuItemData) => {
    const response = await api.post('/menu-items', menuItemData);
    return response.data;
  },

  // Update menu item
  updateMenuItem: async (id, menuItemData) => {
    const response = await api.put(`/menu-items/${id}`, menuItemData);
    return response.data;
  },

  // Delete menu item
  deleteMenuItem: async (id) => {
    const response = await api.delete(`/menu-items/${id}`);
    return response.data;
  },

  // Toggle availability
  toggleAvailability: async (id) => {
    const response = await api.put(`/menu-items/${id}/toggle-availability`);
    return response.data;
  },
};

export default menuService;
