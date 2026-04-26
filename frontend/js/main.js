/**
 * Main JavaScript file for Food Delivery Platform
 * Handles global functionality and page initialization
 */

document.addEventListener('DOMContentLoaded', () => {
    // Initialize the application
    initializeApp();
});

/**
 * Initialize the application
 */
function initializeApp() {
    // Check authentication status and update UI
    authManager.updateUI();
    
    // Initialize page-specific functionality
    initializePageSpecific();
    
    // Setup global event listeners
    setupGlobalEventListeners();
    
    // Setup mobile menu if exists
    setupMobileMenu();
    
    // Setup smooth scrolling
    setupSmoothScrolling();
    
    // Setup lazy loading for images
    setupLazyLoading();
    
    console.log('🍕 FoodDelivery Platform initialized successfully!');
}

/**
 * Initialize page-specific functionality
 */
function initializePageSpecific() {
    const currentPage = getCurrentPage();
    
    switch (currentPage) {
        case 'index':
        case 'home':
            initializeHomePage();
            break;
        case 'restaurant-menu':
            initializeMenuPage();
            break;
        case 'checkout':
            initializeCheckoutPage();
            break;
        case 'orders':
            initializeOrdersPage();
            break;
        case 'profile':
            initializeProfilePage();
            break;
        default:
            console.log(`No specific initialization for page: ${currentPage}`);
    }
}

/**
 * Get current page name from URL
 */
function getCurrentPage() {
    const path = window.location.pathname;
    const page = path.split('/').pop().split('.')[0];
    return page || 'index';
}

/**
 * Initialize home page functionality
 */
function initializeHomePage() {
    // Hero section animations
    animateHeroSection();
    
    // Setup location detection
    setupLocationDetection();
    
    // Note: restaurantManager.init() is called automatically in the constructor
    // and handles authentication check before loading restaurants
}

/**
 * Initialize menu page functionality
 */
function initializeMenuPage() {
    // Load restaurant menu
    loadRestaurantMenu();
}

/**
 * Initialize checkout page functionality
 */
function initializeCheckoutPage() {
    // Redirect if cart is empty
    if (cartManager.isEmpty()) {
        Utils.showToast('Your cart is empty', 'warning');
        window.location.href = '/';
        return;
    }
    
    // Load checkout data
    loadCheckoutData();
}

/**
 * Initialize orders page functionality
 */
function initializeOrdersPage() {
    // Check authentication
    if (!authManager.isAuthenticated()) {
        window.location.href = '/pages/login.html';
        return;
    }
    
    // Load user orders
    loadUserOrders();
}

/**
 * Initialize profile page functionality
 */
function initializeProfilePage() {
    // Check authentication
    if (!authManager.isAuthenticated()) {
        window.location.href = '/pages/login.html';
        return;
    }
    
    // Load user profile
    loadUserProfile();
}

/**
 * Setup global event listeners
 */
function setupGlobalEventListeners() {
    // Handle back button
    window.addEventListener('popstate', (event) => {
        // Handle browser back/forward navigation
        console.log('Navigation event:', event);
    });
    
    // Handle online/offline status
    window.addEventListener('online', () => {
        Utils.showToast('Connection restored', 'success');
    });
    
    window.addEventListener('offline', () => {
        Utils.showToast('Connection lost. Some features may not work.', 'warning');
    });
    
    // Handle keyboard shortcuts
    document.addEventListener('keydown', (e) => {
        // Escape key to close modals/sidebars
        if (e.key === 'Escape') {
            closeAllModals();
        }
        
        // Ctrl/Cmd + K for search
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            focusSearchInput();
        }
    });
    
    // Handle form submissions globally
    document.addEventListener('submit', (e) => {
        // Add loading state to submit buttons
        const submitBtn = e.target.querySelector('button[type="submit"]');
        if (submitBtn && !submitBtn.disabled) {
            addLoadingState(submitBtn);
        }
    });
}

/**
 * Setup mobile menu functionality
 */
function setupMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');
    
    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
        
        // Close mobile menu when clicking outside
        document.addEventListener('click', (e) => {
            if (!mobileMenuBtn.contains(e.target) && !mobileMenu.contains(e.target)) {
                mobileMenu.classList.add('hidden');
            }
        });
    }
}

/**
 * Setup smooth scrolling for anchor links
 */
function setupSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

/**
 * Setup lazy loading for images
 */
function setupLazyLoading() {
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });
        
        document.querySelectorAll('img[data-src]').forEach(img => {
            imageObserver.observe(img);
        });
    }
}

/**
 * Animate hero section
 */
function animateHeroSection() {
    const heroElements = document.querySelectorAll('.hero-animate');
    
    heroElements.forEach((element, index) => {
        setTimeout(() => {
            element.classList.add('animate-fade-in-up');
        }, index * 200);
    });
}

/**
 * Setup location detection
 */
function setupLocationDetection() {
    const locationBtn = document.getElementById('detectLocationBtn');
    
    if (locationBtn) {
        locationBtn.addEventListener('click', detectUserLocation);
    }
    
    // Auto-detect location if not set
    const savedLocation = localStorage.getItem(STORAGE_KEYS.USER_LOCATION);
    if (!savedLocation) {
        detectUserLocation();
    }
}

/**
 * Detect user location
 */
function detectUserLocation() {
    if (!navigator.geolocation) {
        Utils.showToast('Geolocation is not supported by this browser', 'error');
        return;
    }
    
    Utils.showToast('Detecting your location...', 'info');
    
    navigator.geolocation.getCurrentPosition(
        (position) => {
            const { latitude, longitude } = position.coords;
            
            // Save location
            const locationData = { latitude, longitude, timestamp: Date.now() };
            localStorage.setItem(STORAGE_KEYS.USER_LOCATION, JSON.stringify(locationData));
            
            // Reverse geocode to get address
            reverseGeocode(latitude, longitude);
            
            Utils.showToast('Location detected successfully!', 'success');
        },
        (error) => {
            console.error('Geolocation error:', error);
            Utils.showToast('Unable to detect location. Please enter manually.', 'warning');
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 300000 // 5 minutes
        }
    );
}

/**
 * Reverse geocode coordinates to address
 */
async function reverseGeocode(latitude, longitude) {
    try {
        // In a real app, you would use a geocoding service like Google Maps API
        // For demo purposes, we'll just update the location input with coordinates
        const locationInput = document.getElementById('locationInput');
        if (locationInput) {
            locationInput.value = `${latitude.toFixed(4)}, ${longitude.toFixed(4)}`;
        }
    } catch (error) {
        console.error('Reverse geocoding error:', error);
    }
}

/**
 * Load restaurant menu (for menu page)
 */
async function loadRestaurantMenu() {
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get('id');
    
    if (!restaurantId) {
        Utils.showToast('Restaurant not found', 'error');
        window.location.href = '/';
        return;
    }
    
    try {
        const response = await fetch(
            `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.RESTAURANT_MENU.replace('{id}', restaurantId)}`
        );
        
        if (!response.ok) {
            throw new Error('Failed to load menu');
        }
        
        const menuData = await response.json();
        renderRestaurantMenu(menuData);
        
    } catch (error) {
        console.error('Error loading menu:', error);
        Utils.showToast('Failed to load menu', 'error');
    }
}

/**
 * Render restaurant menu
 */
function renderRestaurantMenu(menuData) {
    // Implementation would go here
    console.log('Rendering menu:', menuData);
}

/**
 * Load checkout data
 */
function loadCheckoutData() {
    const cart = cartManager.getCart();
    
    // Populate checkout form with cart data
    populateCheckoutForm(cart);
}

/**
 * Populate checkout form
 */
function populateCheckoutForm(cart) {
    // Implementation would go here
    console.log('Populating checkout form:', cart);
}

/**
 * Load user orders
 */
async function loadUserOrders() {
    try {
        const response = await authManager.apiRequest(API_CONFIG.ENDPOINTS.MY_ORDERS);
        
        if (!response.ok) {
            throw new Error('Failed to load orders');
        }
        
        const orders = await response.json();
        renderUserOrders(orders);
        
    } catch (error) {
        console.error('Error loading orders:', error);
        Utils.showToast('Failed to load orders', 'error');
    }
}

/**
 * Render user orders
 */
function renderUserOrders(orders) {
    // Implementation would go here
    console.log('Rendering orders:', orders);
}

/**
 * Load user profile
 */
function loadUserProfile() {
    const user = authManager.getCurrentUser();
    
    if (user) {
        populateProfileForm(user);
    }
}

/**
 * Populate profile form
 */
function populateProfileForm(user) {
    // Implementation would go here
    console.log('Populating profile form:', user);
}

/**
 * Close all modals and sidebars
 */
function closeAllModals() {
    // Close cart sidebar
    if (typeof cartManager !== 'undefined') {
        cartManager.hideCartSidebar();
    }
    
    // Close any other modals
    document.querySelectorAll('.modal').forEach(modal => {
        modal.classList.add('hidden');
    });
    
    // Close dropdowns
    document.querySelectorAll('.dropdown-menu').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
}

/**
 * Focus search input
 */
function focusSearchInput() {
    const searchInput = document.getElementById('locationInput') || 
                      document.querySelector('input[type="search"]') ||
                      document.querySelector('input[placeholder*="search"]');
    
    if (searchInput) {
        searchInput.focus();
        searchInput.select();
    }
}

/**
 * Add loading state to button
 */
function addLoadingState(button) {
    const originalText = button.textContent;
    button.disabled = true;
    button.innerHTML = `
        <div class="flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
            Loading...
        </div>
    `;
    
    // Remove loading state after form submission completes
    setTimeout(() => {
        button.disabled = false;
        button.textContent = originalText;
    }, 2000);
}

/**
 * Format phone number for display
 */
function formatPhoneNumber(phone) {
    if (!phone) return '';
    
    // Remove all non-digits
    const cleaned = phone.replace(/\D/g, '');
    
    // Format as +91 XXXXX XXXXX
    if (cleaned.length === 10) {
        return `+91 ${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
    }
    
    return phone;
}

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        Utils.showToast('Copied to clipboard!', 'success');
    } catch (error) {
        console.error('Failed to copy:', error);
        Utils.showToast('Failed to copy', 'error');
    }
}

/**
 * Share content using Web Share API
 */
async function shareContent(title, text, url) {
    if (navigator.share) {
        try {
            await navigator.share({ title, text, url });
        } catch (error) {
            console.error('Error sharing:', error);
        }
    } else {
        // Fallback to copying URL
        copyToClipboard(url);
    }
}

/**
 * Handle image loading errors
 */
function handleImageError(img) {
    img.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop';
    img.alt = 'Restaurant image';
}

// Global error handler
window.addEventListener('error', (event) => {
    console.error('Global error:', event.error);
    
    // Don't show error toast for network errors or script loading errors
    if (event.error && !event.error.message.includes('Loading')) {
        Utils.showToast('Something went wrong. Please try again.', 'error');
    }
});

// Global unhandled promise rejection handler
window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
    
    // Prevent the default browser behavior
    event.preventDefault();
    
    // Show user-friendly error message
    Utils.showToast('Something went wrong. Please try again.', 'error');
});