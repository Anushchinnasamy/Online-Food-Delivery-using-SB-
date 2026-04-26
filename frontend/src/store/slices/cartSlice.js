import { createSlice } from '@reduxjs/toolkit';

const loadCartFromStorage = () => {
  try {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : { items: [], restaurant: null };
  } catch (error) {
    return { items: [], restaurant: null };
  }
};

const saveCartToStorage = (cart) => {
  localStorage.setItem('cart', JSON.stringify(cart));
};

const initialState = loadCartFromStorage();

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addToCart: (state, action) => {
      const { menuItem, restaurant } = action.payload;
      
      // If cart is empty or from same restaurant
      if (!state.restaurant || state.restaurant.id === restaurant.id) {
        state.restaurant = restaurant;
        
        const existingItem = state.items.find(item => item.id === menuItem.id);
        if (existingItem) {
          existingItem.quantity += 1;
        } else {
          state.items.push({ ...menuItem, quantity: 1 });
        }
      } else {
        // Different restaurant - clear cart and add new item
        state.items = [{ ...menuItem, quantity: 1 }];
        state.restaurant = restaurant;
      }
      
      saveCartToStorage(state);
    },
    
    removeFromCart: (state, action) => {
      state.items = state.items.filter(item => item.id !== action.payload);
      if (state.items.length === 0) {
        state.restaurant = null;
      }
      saveCartToStorage(state);
    },
    
    updateQuantity: (state, action) => {
      const { id, quantity } = action.payload;
      const item = state.items.find(item => item.id === id);
      if (item) {
        item.quantity = quantity;
        if (quantity <= 0) {
          state.items = state.items.filter(item => item.id !== id);
        }
      }
      if (state.items.length === 0) {
        state.restaurant = null;
      }
      saveCartToStorage(state);
    },
    
    clearCart: (state) => {
      state.items = [];
      state.restaurant = null;
      saveCartToStorage(state);
    },
  },
});

export const { addToCart, removeFromCart, updateQuantity, clearCart } = cartSlice.actions;

export const selectCartTotal = (state) => {
  return state.cart.items.reduce((total, item) => {
    return total + (parseFloat(item.price) * item.quantity);
  }, 0);
};

export const selectCartItemCount = (state) => {
  return state.cart.items.reduce((count, item) => count + item.quantity, 0);
};

export default cartSlice.reducer;
