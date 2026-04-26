import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  restaurants: [],
  currentRestaurant: null,
  loading: false,
  error: null,
};

const restaurantSlice = createSlice({
  name: 'restaurant',
  initialState,
  reducers: {
    setRestaurants: (state, action) => {
      state.restaurants = action.payload;
      state.loading = false;
    },
    setCurrentRestaurant: (state, action) => {
      state.currentRestaurant = action.payload;
      state.loading = false;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
      state.loading = false;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { setRestaurants, setCurrentRestaurant, setLoading, setError, clearError } = restaurantSlice.actions;

export default restaurantSlice.reducer;
