/**
 * Menu module for Restaurant Menu Page
 */

class MenuManager {
    constructor() {
        console.log('MenuManager constructor called');
        this.restaurantId = null;
        this.restaurant = null;
        this.menuItems = [];
        this.init();
    }

    /**
     * Initialize menu manager
     */
    init() {
        console.log('MenuManager init() called');
        
        // Get restaurant ID from URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        this.restaurantId = urlParams.get('id');
        
        console.log('Restaurant ID from URL:', this.restaurantId);
        
        if (!this.restaurantId) {
            console.error('No restaurant ID found in URL');
            this.showError('Restaurant not found');
            return;
        }

        // Load restaurant data and menu
        this.loadData();
    }

    /**
     * Load both restaurant and menu data
     */
    async loadData() {
        try {
            console.log('Loading restaurant and menu data...');
            
            // Load restaurant data
            await this.loadRestaurantData();
            
            // Load menu data
            await this.loadMenu();
            
        } catch (error) {
            console.error('Error loading data:', error);
            this.showError('Failed to load restaurant information');
        }
    }

    /**
     * Load restaurant data from API
     */
    async loadRestaurantData() {
        try {
            console.log('Fetching restaurant from API...');
            const response = await fetch(`http://localhost:8080/restaurants/${this.restaurantId}`);
            console.log('Restaurant API response status:', response.status);
            
            if (response.ok) {
                this.restaurant = await response.json();
                console.log('Restaurant loaded:', this.restaurant);
                this.displayRestaurantInfo();
            } else {
                throw new Error(`Restaurant API returned ${response.status}`);
            }
        } catch (error) {
            console.error('Error loading restaurant data:', error);
            throw error;
        }
    }

    /**
     * Display restaurant information
     */
    displayRestaurantInfo() {
        if (!this.restaurant) return;

        console.log('Displaying restaurant info...');

        // Update restaurant header
        document.getElementById('restaurantName').textContent = this.restaurant.name || 'Restaurant';
        document.getElementById('restaurantDescription').textContent = this.restaurant.description || 'Delicious food awaits you!';
        
        // Update other fields
        const cuisineType = this.restaurant.cuisine_type || this.restaurant.cuisineType || 'Food';
        const rating = this.restaurant.rating || '4.0';
        const deliveryTime = this.restaurant.delivery_time_minutes || this.restaurant.deliveryTimeMinutes || '30';
        const deliveryFee = this.restaurant.delivery_fee || this.restaurant.deliveryFee || '25';
        
        document.getElementById('restaurantCuisine').innerHTML = `<i class="fas fa-utensils mr-1"></i>${cuisineType}`;
        document.getElementById('restaurantRating').innerHTML = `<i class="fas fa-star text-yellow-400 mr-1"></i>${rating}`;
        document.getElementById('restaurantDeliveryTime').innerHTML = `<i class="fas fa-clock mr-1"></i>${deliveryTime} mins`;
        document.getElementById('restaurantDeliveryFee').innerHTML = `<i class="fas fa-truck mr-1"></i>Delivery: ₹${deliveryFee}`;
        
        // Update restaurant image
        const restaurantImage = document.querySelector('#restaurantImage img');
        const imageUrl = this.restaurant.image_url || this.restaurant.imageUrl;
        if (restaurantImage && imageUrl) {
            restaurantImage.src = imageUrl;
            restaurantImage.alt = this.restaurant.name;
            restaurantImage.onerror = function() {
                this.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop';
            };
        }

        // Update page title
        document.title = `${this.restaurant.name} - Menu | Food Delivery`;
        
        console.log('Restaurant info displayed successfully');
    }

    /**
     * Load menu items from API
     */
    async loadMenu() {
        const loadingSpinner = document.getElementById('loadingSpinner');
        const menuItems = document.getElementById('menuItems');
        const errorState = document.getElementById('errorState');
        const emptyState = document.getElementById('emptyState');

        try {
            console.log('Loading menu for restaurant:', this.restaurantId);
            
            // Show loading state
            if (loadingSpinner) loadingSpinner.classList.remove('hidden');
            if (menuItems) menuItems.classList.add('hidden');
            if (errorState) errorState.classList.add('hidden');
            if (emptyState) emptyState.classList.add('hidden');

            // Fetch menu items from API
            const url = `http://localhost:8080/menus/restaurant/${this.restaurantId}`;
            console.log('Fetching menu from:', url);
            
            const response = await fetch(url);
            console.log('Menu API response status:', response.status);
            
            if (!response.ok) {
                throw new Error(`Failed to load menu: ${response.status}`);
            }

            const responseData = await response.json();
            console.log('Menu response data:', responseData);
            
            // Handle response (could be array or paginated)
            this.menuItems = Array.isArray(responseData) ? responseData : (responseData.content || []);
            console.log('Menu items loaded:', this.menuItems.length);
            
            if (this.menuItems.length === 0) {
                this.showEmptyState();
            } else {
                this.displayMenuItems();
            }

        } catch (error) {
            console.error('Error loading menu:', error);
            this.showErrorState();
        } finally {
            if (loadingSpinner) loadingSpinner.classList.add('hidden');
        }
    }

    /**
     * Display menu items
     */
    displayMenuItems() {
        const menuItemsContainer = document.getElementById('menuItems');
        
        if (!menuItemsContainer || this.menuItems.length === 0) {
            this.showEmptyState();
            return;
        }

        console.log('Displaying menu items...');

        let menuHTML = '<div class="p-6">';
        
        // Simple list of menu items
        menuHTML += '<div class="grid gap-4">';
        
        this.menuItems.forEach(item => {
            menuHTML += this.createMenuItemCard(item);
        });
        
        menuHTML += '</div></div>';
        
        menuItemsContainer.innerHTML = menuHTML;
        menuItemsContainer.classList.remove('hidden');
        
        // Setup event listeners for menu items
        this.setupMenuItemListeners();
        
        console.log('Menu items displayed successfully');
    }

    /**
     * Create menu item card HTML
     */
    createMenuItemCard(item) {
        const name = item.name || 'Menu Item';
        const description = item.description || '';
        const price = item.price || 0;
        const imageUrl = item.image_url || item.imageUrl;
        const isVegetarian = item.is_vegetarian || item.isVegetarian;
        const isAvailable = item.is_available !== false; // Default to true

        return `
            <div class="menu-item-card flex items-start p-4 border rounded-lg ${isAvailable ? 'hover:shadow-md transition-shadow' : 'opacity-60'}">
                <div class="flex-1">
                    <div class="flex items-start justify-between mb-2">
                        <h4 class="text-lg font-medium text-gray-900">${name}</h4>
                        <div class="flex items-center ml-4">
                            ${isVegetarian ? '<span class="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Veg</span>' : ''}
                        </div>
                    </div>
                    
                    ${description ? `<p class="text-gray-600 text-sm mb-3">${description}</p>` : ''}
                    
                    <div class="flex items-center justify-between">
                        <span class="text-lg font-semibold text-primary">₹${parseFloat(price).toFixed(2)}</span>
                        
                        ${isAvailable ? `
                            <button class="add-to-cart-btn bg-primary text-white px-4 py-2 rounded hover:bg-orange-600 transition-colors" 
                                    data-item-id="${item.id}" data-item-name="${name}" data-item-price="${price}">
                                <i class="fas fa-plus mr-1"></i>
                                Add to Cart
                            </button>
                        ` : `
                            <span class="text-gray-500 text-sm">Currently Unavailable</span>
                        `}
                    </div>
                </div>
                
                ${imageUrl ? `
                    <div class="w-20 h-20 rounded-lg overflow-hidden ml-4 flex-shrink-0">
                        <img src="${imageUrl}" alt="${name}" 
                             class="w-full h-full object-cover"
                             onerror="this.style.display='none'">
                    </div>
                ` : ''}
            </div>
        `;
    }

    /**
     * Setup event listeners for menu items
     */
    setupMenuItemListeners() {
        document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const itemId = e.currentTarget.dataset.itemId;
                const itemName = e.currentTarget.dataset.itemName;
                const itemPrice = parseFloat(e.currentTarget.dataset.itemPrice);
                
                // Find the full item data
                const item = this.menuItems.find(menuItem => menuItem.id == itemId);
                if (item) {
                    this.addToCart(item);
                } else {
                    console.error('Menu item not found:', itemId);
                }
            });
        });
    }

    /**
     * Add item to cart
     */
    addToCart(menuItem) {
        console.log('Adding item to cart:', menuItem);
        
        // Check if cart manager is available, with a retry mechanism
        const tryAddToCart = () => {
            if (typeof cartManager !== 'undefined' && cartManager) {
                // Add restaurant info to menu item
                const itemWithRestaurant = {
                    id: menuItem.id,
                    name: menuItem.name,
                    description: menuItem.description || '',
                    price: menuItem.price,
                    imageUrl: menuItem.image_url || menuItem.imageUrl,
                    isVegetarian: menuItem.is_vegetarian || menuItem.isVegetarian,
                    isVegan: menuItem.is_vegan || menuItem.isVegan,
                    isSpicy: menuItem.is_spicy || menuItem.isSpicy,
                    restaurantId: this.restaurantId,
                    restaurantName: this.restaurant?.name || 'Restaurant',
                    deliveryFee: this.restaurant?.delivery_fee || this.restaurant?.deliveryFee || 30
                };

                // Use cart manager to add item
                try {
                    cartManager.addItem(itemWithRestaurant);
                    console.log('Item added to cart successfully');
                } catch (error) {
                    console.error('Error adding item to cart:', error);
                    alert('Unable to add item to cart');
                }
            } else {
                console.error('Cart manager not available');
                alert('Please log in to add items to cart');
            }
        };

        // Try immediately, or wait a bit if cart manager is not ready
        if (typeof cartManager !== 'undefined' && cartManager) {
            tryAddToCart();
        } else {
            // Wait a bit for cart manager to initialize
            setTimeout(tryAddToCart, 100);
        }
    }

    /**
     * Show error state
     */
    showErrorState() {
        const elements = ['loadingSpinner', 'menuItems', 'emptyState'];
        elements.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.add('hidden');
        });
        
        const errorState = document.getElementById('errorState');
        if (errorState) errorState.classList.remove('hidden');
    }

    /**
     * Show empty state
     */
    showEmptyState() {
        const elements = ['loadingSpinner', 'menuItems', 'errorState'];
        elements.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.add('hidden');
        });
        
        const emptyState = document.getElementById('emptyState');
        if (emptyState) emptyState.classList.remove('hidden');
    }

    /**
     * Show error message
     */
    showError(message) {
        const restaurantName = document.getElementById('restaurantName');
        const restaurantDescription = document.getElementById('restaurantDescription');
        
        if (restaurantName) restaurantName.textContent = 'Error';
        if (restaurantDescription) restaurantDescription.textContent = message;
        
        this.showErrorState();
    }
}

/**
 * Global functions
 */
function goBack() {
    window.history.back();
}

function loadMenu() {
    if (window.menuManager) {
        menuManager.loadMenu();
    }
}

// Initialize menu manager when page loads
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM Content Loaded - Initializing MenuManager');
    try {
        window.menuManager = new MenuManager();
        console.log('MenuManager initialized successfully');
    } catch (error) {
        console.error('Error initializing MenuManager:', error);
    }
});