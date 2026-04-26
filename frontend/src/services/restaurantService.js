import api from '../config/api';

const restaurantService = {
  // Get all restaurants
  getAllRestaurants: async () => {
    const response = await api.get('/restaurants');
    return response.data;
  },

  // Get restaurant by ID
  getRestaurantById: async (id) => {
    const response = await api.get(`/restaurants/${id}`);
    return response.data;
  },

  // Search restaurants
  searchRestaurants: async (params) => {
    const response = await api.get('/restaurants/search', { params });
    return response.data;
  },

  // Get my restaurant (for restaurant admin)
  getMyRestaurant: async () => {
    const response = await api.get('/restaurants/my');
    return response.data;
  },

  // Create restaurant
  createRestaurant: async (restaurantData) => {
    const response = await api.post('/restaurants', restaurantData);
    return response.data;
  },

  // Update restaurant
  updateRestaurant: async (id, restaurantData) => {
    const response = await api.put(`/restaurants/${id}`, restaurantData);
    return response.data;
  },

  // Delete restaurant
  deleteRestaurant: async (id) => {
    const response = await api.delete(`/restaurants/${id}`);
    return response.data;
  },

  // Approve restaurant (platform admin)
  approveRestaurant: async (id) => {
    const response = await api.put(`/restaurants/${id}/approve`);
    return response.data;
  },
};

export default restaurantService;
