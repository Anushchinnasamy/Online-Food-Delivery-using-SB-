/**
 * Authentication module for Food Delivery Platform
 */

class AuthManager {
    constructor() {
        console.log('🔍 AuthManager: Constructor called');
        this.currentUser = null;
        this.init();
    }

    /**
     * Initialize authentication manager
     */
    init() {
        this.loadUserFromStorage();
        this.setupTokenRefresh();
    }

    /**
     * Load user data from local storage
     */
    loadUserFromStorage() {
        console.log('🔍 AuthManager: Loading user from storage');
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
        const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
        
        console.log('🔍 AuthManager: Token exists?', !!token);
        console.log('🔍 AuthManager: User data exists?', !!userData);
        
        if (token && userData) {
            try {
                this.currentUser = JSON.parse(userData);
                console.log('🔍 AuthManager: User loaded from storage:', this.currentUser.email);
                this.updateUI();
            } catch (error) {
                console.error('Error parsing user data:', error);
                this.logout();
            }
        } else {
            console.log('🔍 AuthManager: No stored user data found');
        }
    }

    /**
     * Login user
     */
    async login(email, password) {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.LOGIN}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Login failed');
            }

            // Store tokens and user data
            localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, data.access_token);
            localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, data.refresh_token);
            localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(data.user));

            this.currentUser = data.user;
            this.updateUI();

            Utils.showToast('Login successful!', 'success');
            
            // Dispatch login event
            document.dispatchEvent(new CustomEvent('userLoggedIn', { detail: data.user }));
            
            // Redirect based on user role
            this.redirectAfterLogin();

            return data;
        } catch (error) {
            console.error('Login error:', error);
            Utils.showToast(error.message, 'error');
            throw error;
        }
    }

    /**
     * Register new user
     */
    async register(userData) {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REGISTER}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Registration failed');
            }

            // Store tokens and user data
            localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, data.access_token);
            localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, data.refresh_token);
            localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(data.user));

            this.currentUser = data.user;
            this.updateUI();

            Utils.showToast('Registration successful!', 'success');
            
            // Dispatch login event
            document.dispatchEvent(new CustomEvent('userLoggedIn', { detail: data.user }));
            
            // Redirect based on user role
            this.redirectAfterLogin();

            return data;
        } catch (error) {
            console.error('Registration error:', error);
            Utils.showToast(error.message, 'error');
            throw error;
        }
    }

    /**
     * Logout user
     */
    logout() {
        // Clear storage
        localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.USER_DATA);
        localStorage.removeItem(STORAGE_KEYS.CART_DATA);

        this.currentUser = null;
        this.updateUI();

        Utils.showToast('Logged out successfully', 'info');
        
        // Dispatch logout event
        document.dispatchEvent(new CustomEvent('userLoggedOut'));
        
        // Redirect to home page
        if (window.location.pathname !== '/' && window.location.pathname !== '/index.html') {
            window.location.href = '/';
        }
    }

    /**
     * Get current user
     */
    getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated() {
        return this.currentUser !== null && localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN) !== null;
    }

    /**
     * Check if user has specific role
     */
    hasRole(role) {
        return this.currentUser && this.currentUser.role === role;
    }

    /**
     * Get authorization header
     */
    getAuthHeader() {
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }

    /**
     * Make authenticated API request
     */
    async apiRequest(url, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...this.getAuthHeader(),
            ...options.headers
        };

        console.log('🔍 Auth Debug: Making API request to:', `${API_CONFIG.BASE_URL}${url}`);
        console.log('🔍 Auth Debug: Headers:', headers);

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${url}`, {
                ...options,
                headers
            });

            console.log('🔍 Auth Debug: Response status:', response.status);

            // Handle token expiration
            if (response.status === 401) {
                console.log('🔍 Auth Debug: 401 received, attempting token refresh');
                const refreshed = await this.refreshToken();
                if (refreshed) {
                    console.log('🔍 Auth Debug: Token refreshed, retrying request');
                    // Retry request with new token
                    const newHeaders = {
                        ...headers,
                        ...this.getAuthHeader()
                    };
                    return fetch(`${API_CONFIG.BASE_URL}${url}`, {
                        ...options,
                        headers: newHeaders
                    });
                } else {
                    console.log('🔍 Auth Debug: Token refresh failed, logging out');
                    this.logout();
                    throw new Error('Authentication required');
                }
            }

            return response;
        } catch (error) {
            console.error('🔍 Auth Debug: API request error:', error);
            throw error;
        }
    }

    /**
     * Refresh access token
     */
    async refreshToken() {
        try {
            const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
            if (!refreshToken) {
                return false;
            }

            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REFRESH}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ refreshToken })
            });

            if (!response.ok) {
                return false;
            }

            const data = await response.json();

            // Update stored tokens
            localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, data.token);
            localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, data.refreshToken);
            localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(data.user));

            this.currentUser = data.user;
            return true;
        } catch (error) {
            console.error('Token refresh error:', error);
            return false;
        }
    }

    /**
     * Setup automatic token refresh
     */
    setupTokenRefresh() {
        // Refresh token every 20 minutes
        setInterval(() => {
            if (this.isAuthenticated()) {
                this.refreshToken();
            }
        }, 20 * 60 * 1000);
    }

    /**
     * Update UI based on authentication state
     */
    updateUI() {
        const userMenu = document.getElementById('userMenu');
        const authButtons = document.getElementById('authButtons');
        const userName = document.getElementById('userName');

        if (this.isAuthenticated() && userMenu && authButtons) {
            userMenu.classList.remove('hidden');
            authButtons.classList.add('hidden');
            
            if (userName) {
                userName.textContent = this.currentUser.name;
            }
        } else if (userMenu && authButtons) {
            userMenu.classList.add('hidden');
            authButtons.classList.remove('hidden');
        }
    }

    /**
     * Redirect user after login based on role
     */
    redirectAfterLogin() {
        const role = this.currentUser.role;
        const currentPath = window.location.pathname;
        
        switch (role) {
            case USER_ROLES.CUSTOMER:
                // Only redirect if not already on home page
                if (currentPath !== '/' && currentPath !== '/index.html') {
                    window.location.href = '/';
                }
                break;
            case USER_ROLES.RESTAURANT_ADMIN:
                window.location.href = '/pages/restaurant-dashboard.html';
                break;
            case USER_ROLES.DELIVERY_PARTNER:
                window.location.href = '/pages/delivery-dashboard.html';
                break;
            case USER_ROLES.PLATFORM_ADMIN:
                window.location.href = '/pages/admin-dashboard.html';
                break;
            default:
                // Only redirect if not already on home page
                if (currentPath !== '/' && currentPath !== '/index.html') {
                    window.location.href = '/';
                }
        }
    }

    /**
     * Change password
     */
    async changePassword(currentPassword, newPassword) {
        try {
            const response = await this.apiRequest(API_CONFIG.ENDPOINTS.CHANGE_PASSWORD, {
                method: 'POST',
                body: JSON.stringify({
                    currentPassword,
                    newPassword
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Password change failed');
            }

            Utils.showToast('Password changed successfully!', 'success');
            return data;
        } catch (error) {
            console.error('Password change error:', error);
            Utils.showToast(error.message, 'error');
            throw error;
        }
    }

    /**
     * Reset password
     */
    async resetPassword(email, newPassword) {
        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.RESET_PASSWORD}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email,
                    newPassword
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Password reset failed');
            }

            Utils.showToast('Password reset successfully!', 'success');
            return data;
        } catch (error) {
            console.error('Password reset error:', error);
            Utils.showToast(error.message, 'error');
            throw error;
        }
    }
}

// Create global auth manager instance
const authManager = new AuthManager();

// Setup event listeners when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // User menu dropdown
    const userMenuBtn = document.getElementById('userMenuBtn');
    const userDropdown = document.getElementById('userDropdown');
    
    if (userMenuBtn && userDropdown) {
        userMenuBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            userDropdown.classList.toggle('hidden');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', () => {
            userDropdown.classList.add('hidden');
        });
    }

    // Logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            authManager.logout();
        });
    }
});