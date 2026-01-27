/**
 * Configuration file for the Food Delivery Platform frontend
 * Updated for Microservices Architecture
 */

// API Configuration - Now points to API Gateway
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080', // API Gateway URL
    ENDPOINTS: {
        // Authentication (Auth Service via Gateway)
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        REFRESH: '/auth/refresh',
        ME: '/auth/me',
        VALIDATE: '/auth/validate',
        CHANGE_PASSWORD: '/auth/change-password',
        RESET_PASSWORD: '/auth/reset-password',
        LOGOUT: '/auth/logout',
        
        // Users (User Service via Gateway)
        USERS: '/users',
        USER_PROFILE: '/users/profile',
        USER_ADDRESSES: '/users/addresses',
        
        // Restaurants (Restaurant Service via Gateway)
        RESTAURANTS: '/restaurants',
        RESTAURANT_DETAIL: '/restaurants/{id}',
        RESTAURANT_MENU: '/restaurants/{id}/menu',
        RESTAURANT_SEARCH: '/restaurants/search',
        RESTAURANT_NEARBY: '/restaurants/nearby',
        
        // Menu Items (Menu Service via Gateway)
        MENU_ITEMS: '/menus',
        MENU_ITEM_SEARCH: '/menus/search',
        MENU_CATEGORIES: '/menus/categories',
        
        // Cart (Cart Service via Gateway)
        CART: '/cart',
        CART_ADD: '/cart/add',
        CART_UPDATE: '/cart/update',
        CART_REMOVE: '/cart/remove',
        CART_CLEAR: '/cart/clear',
        
        // Orders (Order Service via Gateway)
        ORDERS: '/orders',
        MY_ORDERS: '/orders/my',
        ORDER_DETAIL: '/orders/{id}',
        ORDER_CANCEL: '/orders/{id}/cancel',
        ORDER_STATUS: '/orders/{id}/status',
        ORDER_TRACK: '/orders/{id}/track',
        
        // Payments (Payment Service via Gateway)
        PAYMENTS: '/payments',
        PAYMENT_INITIATE: '/payments/initiate',
        PAYMENT_STATUS: '/payments/{id}/status',
        PAYMENT_METHODS: '/payments/methods',
        
        // Delivery (Delivery Service via Gateway)
        DELIVERY: '/delivery',
        DELIVERY_STATUS: '/delivery/{id}/status',
        DELIVERY_TRACK: '/delivery/{id}/track',
        DELIVERY_HISTORY: '/delivery/history',
        
        // Restaurant Admin (Restaurant Service via Gateway)
        RESTAURANT_ORDERS: '/restaurants/orders',
        RESTAURANT_MENU_MANAGE: '/restaurants/menu',
        RESTAURANT_PROFILE: '/restaurants/profile',
        RESTAURANT_ANALYTICS: '/restaurants/analytics',
        
        // Delivery Partner (Delivery Service via Gateway)
        DELIVERY_AVAILABLE: '/delivery/available',
        DELIVERY_ACCEPT: '/delivery/{id}/accept',
        DELIVERY_PICKUP: '/delivery/{id}/pickup',
        DELIVERY_COMPLETE: '/delivery/{id}/complete',
        
        // Admin (Reporting Service via Gateway)
        ADMIN_DASHBOARD: '/reports/dashboard',
        ADMIN_USERS: '/users/admin',
        ADMIN_RESTAURANTS: '/restaurants/admin',
        ADMIN_ANALYTICS: '/analytics',
        
        // Health Checks
        HEALTH: '/actuator/health'
    }
};

// Storage Keys for localStorage
const STORAGE_KEYS = {
    ACCESS_TOKEN: 'fd_access_token',
    REFRESH_TOKEN: 'fd_refresh_token',
    USER_DATA: 'fd_user_data',
    CART_DATA: 'fd_cart_data',
    LAST_LOCATION: 'fd_last_location',
    USER_LOCATION: 'fd_user_location', // For backward compatibility
    PREFERENCES: 'fd_preferences'
};

// User Roles
const USER_ROLES = {
    CUSTOMER: 'CUSTOMER',
    RESTAURANT_ADMIN: 'RESTAURANT_ADMIN',
    DELIVERY_PARTNER: 'DELIVERY_PARTNER',
    PLATFORM_ADMIN: 'PLATFORM_ADMIN'
};

// Order Status
const ORDER_STATUS = {
    PLACED: 'PLACED',
    ACCEPTED: 'ACCEPTED',
    PREPARING: 'PREPARING',
    READY_FOR_PICKUP: 'READY_FOR_PICKUP',
    PICKED_UP: 'PICKED_UP',
    OUT_FOR_DELIVERY: 'OUT_FOR_DELIVERY',
    DELIVERED: 'DELIVERED',
    CANCELLED: 'CANCELLED'
};

// Payment Status
const PAYMENT_STATUS = {
    PENDING: 'PENDING',
    PROCESSING: 'PROCESSING',
    SUCCESS: 'SUCCESS',
    FAILED: 'FAILED',
    REFUNDED: 'REFUNDED'
};

// Payment Methods
const PAYMENT_METHODS = {
    CASH_ON_DELIVERY: 'CASH_ON_DELIVERY',
    CREDIT_CARD: 'CREDIT_CARD',
    DEBIT_CARD: 'DEBIT_CARD',
    UPI: 'UPI',
    NET_BANKING: 'NET_BANKING',
    WALLET: 'WALLET'
};

// Delivery Status
const DELIVERY_STATUS = {
    ASSIGNED: 'ASSIGNED',
    ACCEPTED: 'ACCEPTED',
    PICKED_UP: 'PICKED_UP',
    OUT_FOR_DELIVERY: 'OUT_FOR_DELIVERY',
    DELIVERED: 'DELIVERED',
    CANCELLED: 'CANCELLED'
};

// Microservices Configuration
const MICROSERVICES_CONFIG = {
    GATEWAY_URL: 'http://localhost:8080',
    SERVICES: {
        AUTH: 'http://localhost:8081',
        USER: 'http://localhost:8082',
        RESTAURANT: 'http://localhost:8083',
        CART: 'http://localhost:8084',
        ORDER: 'http://localhost:8085',
        PAYMENT: 'http://localhost:8086',
        DELIVERY: 'http://localhost:8087',
        REPORTING: 'http://localhost:8088'
    },
    DISCOVERY: 'http://localhost:8761',
    CONFIG: 'http://localhost:8888'
};

// Utility Functions
const Utils = {
    /**
     * Format currency amount
     */
    formatCurrency: (amount, currency = 'INR') => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 2
        }).format(amount);
    },

    /**
     * Format date and time
     */
    formatDateTime: (dateTime) => {
        return new Intl.DateTimeFormat('en-IN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }).format(new Date(dateTime));
    },

    /**
     * Format relative time (e.g., "2 hours ago")
     */
    formatRelativeTime: (dateTime) => {
        const rtf = new Intl.RelativeTimeFormat('en', { numeric: 'auto' });
        const diff = new Date(dateTime) - new Date();
        const diffInMinutes = Math.floor(diff / (1000 * 60));
        
        if (Math.abs(diffInMinutes) < 60) {
            return rtf.format(diffInMinutes, 'minute');
        } else if (Math.abs(diffInMinutes) < 1440) {
            return rtf.format(Math.floor(diffInMinutes / 60), 'hour');
        } else {
            return rtf.format(Math.floor(diffInMinutes / 1440), 'day');
        }
    },

    /**
     * Debounce function for search inputs
     */
    debounce: (func, wait) => {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    /**
     * Show toast notification
     */
    showToast: (message, type = 'info', duration = 3000) => {
        // Create toast element
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        
        // Add to DOM
        document.body.appendChild(toast);
        
        // Show toast
        setTimeout(() => toast.classList.add('show'), 100);
        
        // Hide and remove toast
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => document.body.removeChild(toast), 300);
        }, duration);
    },

    /**
     * Get user's current location
     */
    getCurrentLocation: () => {
        return new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject(new Error('Geolocation is not supported'));
                return;
            }
            
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    resolve({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                },
                (error) => {
                    reject(error);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 300000 // 5 minutes
                }
            );
        });
    },

    /**
     * Calculate distance between two coordinates
     */
    calculateDistance: (lat1, lon1, lat2, lon2) => {
        const R = 6371; // Radius of the Earth in km
        const dLat = (lat2 - lat1) * Math.PI / 180;
        const dLon = (lon2 - lon1) * Math.PI / 180;
        const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                  Math.sin(dLon/2) * Math.sin(dLon/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; // Distance in km
    },

    /**
     * Validate email format
     */
    isValidEmail: (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    /**
     * Validate phone number format (Indian)
     */
    isValidPhone: (phone) => {
        const phoneRegex = /^[6-9]\d{9}$/;
        return phoneRegex.test(phone.replace(/\D/g, ''));
    },

    /**
     * Generate unique ID
     */
    generateId: () => {
        return Date.now().toString(36) + Math.random().toString(36).substring(2);
    },

    /**
     * Deep clone object
     */
    deepClone: (obj) => {
        return JSON.parse(JSON.stringify(obj));
    },

    /**
     * Check if user is online
     */
    isOnline: () => {
        return navigator.onLine;
    },

    /**
     * Get order status color
     */
    getOrderStatusColor: (status) => {
        const colors = {
            [ORDER_STATUS.PLACED]: 'blue',
            [ORDER_STATUS.ACCEPTED]: 'green',
            [ORDER_STATUS.PREPARING]: 'yellow',
            [ORDER_STATUS.READY_FOR_PICKUP]: 'orange',
            [ORDER_STATUS.PICKED_UP]: 'purple',
            [ORDER_STATUS.OUT_FOR_DELIVERY]: 'indigo',
            [ORDER_STATUS.DELIVERED]: 'green',
            [ORDER_STATUS.CANCELLED]: 'red'
        };
        return colors[status] || 'gray';
    },

    /**
     * Get payment status color
     */
    getPaymentStatusColor: (status) => {
        const colors = {
            [PAYMENT_STATUS.PENDING]: 'yellow',
            [PAYMENT_STATUS.PROCESSING]: 'blue',
            [PAYMENT_STATUS.SUCCESS]: 'green',
            [PAYMENT_STATUS.FAILED]: 'red',
            [PAYMENT_STATUS.REFUNDED]: 'orange'
        };
        return colors[status] || 'gray';
    }
};

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { 
        API_CONFIG, 
        STORAGE_KEYS, 
        USER_ROLES, 
        ORDER_STATUS, 
        PAYMENT_STATUS,
        PAYMENT_METHODS, 
        DELIVERY_STATUS,
        MICROSERVICES_CONFIG, 
        Utils 
    };
}