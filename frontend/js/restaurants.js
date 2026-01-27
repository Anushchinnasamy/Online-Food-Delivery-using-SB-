/**
 * Restaurants module for Food Delivery Platform
 */

class RestaurantManager {
    constructor() {
        console.log('🔍 RestaurantManager: Constructor called');
        this.restaurants = [];
        this.filteredRestaurants = [];
        this.currentFilter = 'all';
        this.init();
    }

    /**
     * Initialize restaurant manager
     */
    init() {
        this.setupEventListeners();
        
        // Always load restaurants (no authentication required for browsing)
        console.log('🔍 RestaurantManager: Loading restaurants (public access)');
        this.loadRestaurants();
        
        // Listen for authentication state changes (for UI updates)
        this.setupAuthListener();
    }

    /**
     * Show login prompt
     */
    showLoginPrompt() {
        const restaurantsGrid = document.getElementById('restaurantsGrid');
        if (restaurantsGrid) {
            restaurantsGrid.innerHTML = `
                <div class="col-span-full text-center py-8">
                    <i class="fas fa-sign-in-alt text-4xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500">Please log in to view restaurants</p>
                    <a href="pages/login.html" class="mt-2 inline-block bg-primary text-white px-4 py-2 rounded hover:bg-orange-600">
                        Login
                    </a>
                </div>
            `;
        }
    }

    /**
     * Setup authentication state listener
     */
    setupAuthListener() {
        // Listen for storage changes (when user logs in/out in another tab)
        window.addEventListener('storage', (e) => {
            if (e.key === STORAGE_KEYS.ACCESS_TOKEN) {
                // Just update UI, restaurants are always visible
                console.log('🔍 RestaurantManager: Auth state changed in another tab');
            }
        });
        
        // Listen for custom auth events (for UI updates only)
        document.addEventListener('userLoggedIn', () => {
            console.log('🔍 RestaurantManager: userLoggedIn event received (UI update only)');
            // No need to reload restaurants, they're already loaded
        });
        
        document.addEventListener('userLoggedOut', () => {
            console.log('🔍 RestaurantManager: userLoggedOut event received (UI update only)');
            // No need to hide restaurants, they remain visible
        });
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Filter buttons
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const filter = e.target.dataset.filter;
                this.filterRestaurants(filter);
                this.updateFilterButtons(e.target);
            });
        });

        // Search functionality
        const searchBtn = document.getElementById('searchBtn');
        const locationInput = document.getElementById('locationInput');
        
        if (searchBtn && locationInput) {
            searchBtn.addEventListener('click', () => {
                this.searchRestaurants(locationInput.value);
            });

            locationInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.searchRestaurants(locationInput.value);
                }
            });
        }
    }

    /**
     * Load restaurants from API
     */
    async loadRestaurants() {
        const loadingSpinner = document.getElementById('loadingSpinner');
        const restaurantsGrid = document.getElementById('restaurantsGrid');

        try {
            if (loadingSpinner) loadingSpinner.classList.remove('hidden');

            console.log('🔍 Debug: Loading restaurants (public access)');

            // Use regular fetch for public endpoints (no authentication required)
            const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.RESTAURANTS}`);
            
            console.log('🔍 Debug: Response status:', response.status);
            console.log('🔍 Debug: Response ok:', response.ok);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.log('🔍 Debug: Error response:', errorText);
                throw new Error(`Failed to load restaurants: ${response.status} - ${errorText}`);
            }

            const responseData = await response.json();
            console.log('🔍 Debug: Response data:', responseData);
            
            // Handle paginated response (Spring Data format)
            this.restaurants = responseData.content || responseData;
            this.filteredRestaurants = [...this.restaurants];
            console.log('🔍 Debug: Restaurants loaded:', this.restaurants.length);
            this.renderRestaurants();

        } catch (error) {
            console.error('❌ Error loading restaurants:', error);
            if (restaurantsGrid) {
                restaurantsGrid.innerHTML = `
                    <div class="col-span-full text-center py-8">
                        <i class="fas fa-exclamation-triangle text-4xl text-gray-300 mb-4"></i>
                        <p class="text-gray-500">Failed to load restaurants</p>
                        <p class="text-sm text-red-500 mt-2">${error.message}</p>
                        <button onclick="restaurantManager.loadRestaurants()" class="mt-2 text-primary hover:text-orange-600">
                            Try Again
                        </button>
                    </div>
                `;
            }
        } finally {
            if (loadingSpinner) loadingSpinner.classList.add('hidden');
        }
    }

    /**
     * Filter restaurants by cuisine type
     */
    filterRestaurants(filter) {
        this.currentFilter = filter;
        
        if (filter === 'all') {
            this.filteredRestaurants = [...this.restaurants];
        } else {
            this.filteredRestaurants = this.restaurants.filter(restaurant => {
                const cuisineType = restaurant.cuisine_type || restaurant.cuisineType;
                return cuisineType === filter;
            });
        }
        
        this.renderRestaurants();
    }

    /**
     * Update filter button states
     */
    updateFilterButtons(activeBtn) {
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active', 'bg-primary', 'text-white');
            btn.classList.add('bg-white', 'text-gray-700');
        });
        
        activeBtn.classList.add('active', 'bg-primary', 'text-white');
        activeBtn.classList.remove('bg-white', 'text-gray-700');
    }

    /**
     * Search restaurants by location or name
     */
    async searchRestaurants(query) {
        if (!query.trim()) {
            this.filteredRestaurants = [...this.restaurants];
            this.renderRestaurants();
            return;
        }

        try {
            // Use regular fetch for public endpoints (no authentication required)
            const response = await fetch(
                `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.RESTAURANT_SEARCH}?q=${encodeURIComponent(query)}`
            );
            
            if (response.ok) {
                const responseData = await response.json();
                // Handle paginated response (Spring Data format)
                this.filteredRestaurants = responseData.content || responseData;
            } else {
                // Fallback to client-side search
                this.filteredRestaurants = this.restaurants.filter(restaurant => {
                    const name = restaurant.name.toLowerCase();
                    const city = restaurant.city.toLowerCase();
                    const cuisineType = (restaurant.cuisine_type || restaurant.cuisineType).toLowerCase();
                    const searchQuery = query.toLowerCase();
                    
                    return name.includes(searchQuery) || 
                           city.includes(searchQuery) || 
                           cuisineType.includes(searchQuery);
                });
            }
            
            this.renderRestaurants();
        } catch (error) {
            console.error('Search error:', error);
            Utils.showToast('Search failed. Please try again.', 'error');
        }
    }

    /**
     * Render restaurants grid
     */
    renderRestaurants() {
        const restaurantsGrid = document.getElementById('restaurantsGrid');
        if (!restaurantsGrid) return;

        if (this.filteredRestaurants.length === 0) {
            restaurantsGrid.innerHTML = `
                <div class="col-span-full text-center py-8">
                    <i class="fas fa-search text-4xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500">No restaurants found</p>
                    <p class="text-sm text-gray-400 mt-2">Try adjusting your search or filters</p>
                </div>
            `;
            return;
        }

        const restaurantsHTML = this.filteredRestaurants.map(restaurant => 
            this.createRestaurantCard(restaurant)
        ).join('');

        restaurantsGrid.innerHTML = restaurantsHTML;

        // Add click listeners to restaurant cards
        this.setupRestaurantCardListeners();
    }

    /**
     * Create restaurant card HTML
     */
    createRestaurantCard(restaurant) {
        // Map API fields to frontend format
        const mappedRestaurant = {
            id: restaurant.id,
            name: restaurant.name,
            city: restaurant.city,
            rating: restaurant.rating,
            cuisineType: restaurant.cuisine_type || restaurant.cuisineType,
            deliveryTimeMinutes: restaurant.delivery_time_minutes || restaurant.deliveryTimeMinutes,
            deliveryFee: restaurant.delivery_fee || restaurant.deliveryFee,
            isOpen: restaurant.is_open !== undefined ? restaurant.is_open : restaurant.isOpen,
            isActive: restaurant.is_active !== undefined ? restaurant.is_active : restaurant.isActive,
            isApproved: restaurant.is_approved !== undefined ? restaurant.is_approved : restaurant.isApproved,
            imageUrl: restaurant.image_url || restaurant.imageUrl,
            minimumOrderAmount: restaurant.minimum_order_amount || restaurant.minimumOrderAmount,
            description: restaurant.description
        };
        
        // For customer view, only check isOpen since backend already filters active/approved restaurants
        const isOpen = mappedRestaurant.isOpen;
        const statusClass = isOpen ? 'text-green-600' : 'text-red-600';
        const statusText = isOpen ? 'Open' : 'Closed';
        const statusIcon = isOpen ? 'fa-clock' : 'fa-times-circle';

        return `
            <div class="restaurant-card bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-300 cursor-pointer" 
                 data-restaurant-id="${mappedRestaurant.id}">
                <div class="relative">
                    <img src="${mappedRestaurant.imageUrl || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop'}" 
                         alt="${mappedRestaurant.name}" 
                         class="w-full h-48 object-cover"
                         onerror="this.src='https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop'">
                    <div class="absolute top-2 right-2 bg-white px-2 py-1 rounded-full text-xs font-medium ${statusClass}">
                        <i class="fas ${statusIcon} mr-1"></i>
                        ${statusText}
                    </div>
                    ${mappedRestaurant.deliveryTimeMinutes ? `
                        <div class="absolute bottom-2 left-2 bg-black bg-opacity-70 text-white px-2 py-1 rounded text-xs">
                            <i class="fas fa-clock mr-1"></i>
                            ${mappedRestaurant.deliveryTimeMinutes} mins
                        </div>
                    ` : ''}
                </div>
                
                <div class="p-4">
                    <div class="flex justify-between items-start mb-2">
                        <h3 class="text-lg font-semibold text-gray-900 truncate">${mappedRestaurant.name}</h3>
                        <div class="flex items-center ml-2">
                            <i class="fas fa-star text-yellow-400 text-sm"></i>
                            <span class="text-sm text-gray-600 ml-1">${mappedRestaurant.rating || '4.0'}</span>
                        </div>
                    </div>
                    
                    <p class="text-gray-600 text-sm mb-2 line-clamp-2">${mappedRestaurant.description || 'Delicious food awaits you!'}</p>
                    
                    <div class="flex items-center justify-between text-sm text-gray-500 mb-3">
                        <span class="flex items-center">
                            <i class="fas fa-utensils mr-1"></i>
                            ${mappedRestaurant.cuisineType}
                        </span>
                        <span class="flex items-center">
                            <i class="fas fa-map-marker-alt mr-1"></i>
                            ${mappedRestaurant.city}
                        </span>
                    </div>
                    
                    <div class="flex items-center justify-between text-sm">
                        <span class="text-gray-600">
                            Min. order: ${Utils.formatCurrency(mappedRestaurant.minimumOrderAmount || 0)}
                        </span>
                        <span class="text-gray-600">
                            Delivery: ${Utils.formatCurrency(mappedRestaurant.deliveryFee || 0)}
                        </span>
                    </div>
                    
                    ${!isOpen ? `
                        <div class="mt-3 p-2 bg-red-50 border border-red-200 rounded text-center">
                            <span class="text-red-600 text-sm">Currently Closed</span>
                        </div>
                    ` : ''}
                </div>
            </div>
        `;
    }

    /**
     * Setup restaurant card click listeners
     */
    setupRestaurantCardListeners() {
        document.querySelectorAll('.restaurant-card').forEach(card => {
            card.addEventListener('click', () => {
                const restaurantId = card.dataset.restaurantId;
                this.openRestaurantMenu(restaurantId);
            });
        });
    }

    /**
     * Open restaurant menu page
     */
    openRestaurantMenu(restaurantId) {
        const restaurant = this.restaurants.find(r => r.id == restaurantId);
        
        if (!restaurant) {
            Utils.showToast('Restaurant not found', 'error');
            return;
        }

        // For customer view, only check isOpen since backend already filters active/approved restaurants
        if (!restaurant.is_open && !restaurant.isOpen) {
            Utils.showToast('This restaurant is currently closed', 'warning');
            return;
        }

        // Store restaurant data for menu page
        sessionStorage.setItem('selectedRestaurant', JSON.stringify(restaurant));
        
        // Navigate to menu page
        window.location.href = `restaurant-menu.html?id=${restaurantId}`;
    }

    /**
     * Get restaurant by ID
     */
    getRestaurantById(id) {
        return this.restaurants.find(r => r.id == id);
    }

    /**
     * Get filtered restaurants
     */
    getFilteredRestaurants() {
        return this.filteredRestaurants;
    }
}

// Create global restaurant manager instance
const restaurantManager = new RestaurantManager();