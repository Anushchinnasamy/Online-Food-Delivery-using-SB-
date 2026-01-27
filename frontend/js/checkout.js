/**
 * Checkout module for Food Delivery Platform
 */

class CheckoutManager {
    constructor() {
        this.cart = null;
        this.user = null;
        this.init();
    }

    /**
     * Initialize checkout manager
     */
    init() {
        console.log('CheckoutManager initialized');
        
        // Check if user is authenticated
        if (!this.checkAuthentication()) {
            return;
        }

        // Load cart data
        this.loadCartData();
        
        // Setup event listeners
        this.setupEventListeners();
        
        // Display checkout content
        this.displayCheckout();
    }

    /**
     * Check if user is authenticated
     */
    checkAuthentication() {
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
        if (!token) {
            // Redirect to login
            window.location.href = 'login.html?redirect=checkout';
            return false;
        }
        
        // Load user data
        const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
        if (userData) {
            try {
                this.user = JSON.parse(userData);
            } catch (error) {
                console.error('Error parsing user data:', error);
            }
        }
        
        return true;
    }

    /**
     * Load cart data
     */
    loadCartData() {
        const cartData = localStorage.getItem(STORAGE_KEYS.CART_DATA);
        if (cartData) {
            try {
                this.cart = JSON.parse(cartData);
                console.log('Cart data loaded:', this.cart);
            } catch (error) {
                console.error('Error parsing cart data:', error);
                this.showError();
                return;
            }
        }

        // Check if cart is empty
        if (!this.cart || !this.cart.items || this.cart.items.length === 0) {
            this.showEmptyCart();
            return;
        }
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Place order button
        const placeOrderBtn = document.getElementById('placeOrderBtn');
        if (placeOrderBtn) {
            placeOrderBtn.addEventListener('click', () => {
                this.placeOrder();
            });
        }

        // Phone number validation
        const phoneInput = document.getElementById('phoneNumber');
        if (phoneInput) {
            phoneInput.addEventListener('input', (e) => {
                // Only allow digits and limit to 10 characters
                e.target.value = e.target.value.replace(/\D/g, '').slice(0, 10);
                this.validateForm();
            });
        }

        // Address validation
        const addressInput = document.getElementById('deliveryAddress');
        if (addressInput) {
            addressInput.addEventListener('input', () => {
                this.validateForm();
            });
        }
    }

    /**
     * Display checkout content
     */
    displayCheckout() {
        // Hide loading state
        document.getElementById('loadingState').classList.add('hidden');
        
        // Show checkout content
        document.getElementById('checkoutContent').classList.remove('hidden');
        
        // Populate order items
        this.displayOrderItems();
        
        // Populate order summary
        this.displayOrderSummary();
        
        // Pre-fill user data if available
        this.prefillUserData();
        
        // Validate form initially
        this.validateForm();
    }

    /**
     * Display order items
     */
    displayOrderItems() {
        const orderItemsContainer = document.getElementById('orderItems');
        if (!orderItemsContainer || !this.cart.items) return;

        let itemsHTML = '';
        
        this.cart.items.forEach(item => {
            itemsHTML += `
                <div class="flex items-center justify-between py-3 border-b last:border-b-0">
                    <div class="flex items-center flex-1">
                        <div class="flex-1">
                            <h4 class="font-medium text-gray-900">${item.name}</h4>
                            ${item.specialInstructions ? `<p class="text-xs text-gray-500 mt-1">Note: ${item.specialInstructions}</p>` : ''}
                            <div class="flex items-center mt-1">
                                ${item.isVegetarian ? '<span class="text-xs bg-green-100 text-green-800 px-2 py-1 rounded mr-1">Veg</span>' : ''}
                                ${item.isSpicy ? '<span class="text-xs bg-red-100 text-red-800 px-2 py-1 rounded">Spicy</span>' : ''}
                            </div>
                        </div>
                    </div>
                    <div class="text-right ml-4">
                        <div class="font-medium text-gray-900">₹${(item.price * item.quantity).toFixed(2)}</div>
                        <div class="text-sm text-gray-500">₹${item.price} × ${item.quantity}</div>
                    </div>
                </div>
            `;
        });

        orderItemsContainer.innerHTML = itemsHTML;
    }

    /**
     * Display order summary
     */
    displayOrderSummary() {
        // Restaurant info
        const restaurantInfo = document.getElementById('restaurantInfo');
        if (restaurantInfo && this.cart.restaurantName) {
            restaurantInfo.innerHTML = `
                <div class="flex items-center">
                    <i class="fas fa-store text-primary mr-2"></i>
                    <span class="font-medium">${this.cart.restaurantName}</span>
                </div>
            `;
        }

        // Price breakdown
        document.getElementById('subtotalAmount').textContent = `₹${this.cart.subtotal.toFixed(2)}`;
        document.getElementById('deliveryFeeAmount').textContent = `₹${this.cart.deliveryFee.toFixed(2)}`;
        document.getElementById('taxAmount').textContent = `₹${this.cart.tax.toFixed(2)}`;
        document.getElementById('totalAmount').textContent = `₹${this.cart.total.toFixed(2)}`;

        // Estimated delivery time
        const estimatedDelivery = document.getElementById('estimatedDelivery');
        if (estimatedDelivery) {
            // You could calculate this based on restaurant data
            estimatedDelivery.textContent = '30-45 mins';
        }
    }

    /**
     * Pre-fill user data
     */
    prefillUserData() {
        if (this.user) {
            // Pre-fill phone number if available
            const phoneInput = document.getElementById('phoneNumber');
            if (phoneInput && this.user.phone) {
                phoneInput.value = this.user.phone;
            }
        }
    }

    /**
     * Validate form
     */
    validateForm() {
        const address = document.getElementById('deliveryAddress').value.trim();
        const phone = document.getElementById('phoneNumber').value.trim();
        const placeOrderBtn = document.getElementById('placeOrderBtn');

        const isValid = address.length >= 10 && phone.length === 10;
        
        if (placeOrderBtn) {
            placeOrderBtn.disabled = !isValid;
        }

        return isValid;
    }

    /**
     * Place order
     */
    async placeOrder() {
        if (!this.validateForm()) {
            alert('Please fill in all required fields correctly.');
            return;
        }

        const placeOrderBtn = document.getElementById('placeOrderBtn');
        const originalText = placeOrderBtn.innerHTML;
        
        try {
            // Show loading state
            placeOrderBtn.disabled = true;
            placeOrderBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Placing Order...';

            // Prepare order data for Order Service API
            const orderData = {
                restaurantId: this.cart.restaurantId,
                items: this.cart.items.map(item => ({
                    menuItemId: item.id,
                    quantity: item.quantity,
                    specialInstructions: item.specialInstructions || ''
                })),
                specialInstructions: `Delivery Address: ${document.getElementById('deliveryAddress').value.trim()}\nPhone: ${document.getElementById('phoneNumber').value.trim()}\nLandmark: ${document.getElementById('landmark').value.trim()}\nPayment Method: ${document.querySelector('input[name="paymentMethod"]:checked').value}`
            };

            console.log('Placing order:', orderData);

            // Call the actual Order Service API
            const response = await fetch(`${API_CONFIG.BASE_URL}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)}`
                },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Order placement failed: ${response.status}`);
            }

            const orderResponse = await response.json();
            console.log('Order placed successfully:', orderResponse);

            // Clear cart
            localStorage.removeItem(STORAGE_KEYS.CART_DATA);

            // Show success modal
            this.showOrderSuccess(orderResponse);

        } catch (error) {
            console.error('Error placing order:', error);
            alert(`Failed to place order: ${error.message}`);
        } finally {
            // Restore button state
            placeOrderBtn.disabled = false;
            placeOrderBtn.innerHTML = originalText;
        }
    }

    /**
     * Show order success modal
     */
    showOrderSuccess(orderResponse) {
        // Update modal with order details if available
        if (orderResponse && orderResponse.orderNumber) {
            const modal = document.getElementById('orderSuccessModal');
            const orderNumberElement = modal.querySelector('.order-number');
            if (orderNumberElement) {
                orderNumberElement.textContent = orderResponse.orderNumber;
            } else {
                // Add order number to modal
                const modalContent = modal.querySelector('.text-center');
                const orderInfo = document.createElement('p');
                orderInfo.className = 'text-gray-600 mb-2';
                orderInfo.innerHTML = `<strong>Order Number:</strong> ${orderResponse.orderNumber}`;
                modalContent.insertBefore(orderInfo, modalContent.querySelector('.space-y-3'));
            }
        }
        
        document.getElementById('orderSuccessModal').classList.remove('hidden');
    }

    /**
     * Show empty cart state
     */
    showEmptyCart() {
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('emptyCartState').classList.remove('hidden');
    }

    /**
     * Show error state
     */
    showError() {
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('errorState').classList.remove('hidden');
    }
}

/**
 * Global functions
 */
function goBack() {
    window.history.back();
}

function goToOrders() {
    // In a real app, navigate to orders page
    alert('Order tracking feature coming soon!');
    goHome();
}

function goHome() {
    window.location.href = '../index.html';
}

// Initialize checkout manager when page loads
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM Content Loaded - Initializing CheckoutManager');
    try {
        window.checkoutManager = new CheckoutManager();
        console.log('CheckoutManager initialized successfully');
    } catch (error) {
        console.error('Error initializing CheckoutManager:', error);
    }
});