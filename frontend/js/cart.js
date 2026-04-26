/**
 * Shopping Cart module for Food Delivery Platform
 */

class CartManager {
    constructor() {
        this.cart = {
            items: [],
            restaurantId: null,
            restaurantName: '',
            subtotal: 0,
            deliveryFee: 0,
            tax: 0,
            total: 0
        };
        this.init();
    }

    /**
     * Initialize cart manager
     */
    init() {
        this.loadCartFromStorage();
        this.setupEventListeners();
        this.updateCartUI();
    }

    /**
     * Load cart data from local storage
     */
    loadCartFromStorage() {
        const cartData = localStorage.getItem(STORAGE_KEYS.CART_DATA);
        if (cartData) {
            try {
                this.cart = JSON.parse(cartData);
                this.calculateTotals();
            } catch (error) {
                console.error('Error parsing cart data:', error);
                this.clearCart();
            }
        }
    }

    /**
     * Save cart data to local storage
     */
    saveCartToStorage() {
        localStorage.setItem(STORAGE_KEYS.CART_DATA, JSON.stringify(this.cart));
    }

    /**
     * Add item to cart
     */
    addItem(menuItem, quantity = 1, specialInstructions = '') {
        // Check authentication first - users must be logged in to add items to cart
        if (!authManager.isAuthenticated()) {
            Utils.showToast('Please log in to add items to cart', 'warning');
            // Redirect to login page or show login modal
            window.location.href = 'pages/login.html';
            return false;
        }

        // Check if item is from different restaurant
        if (this.cart.restaurantId && this.cart.restaurantId !== menuItem.restaurantId) {
            const confirmSwitch = confirm(
                `Your cart contains items from ${this.cart.restaurantName}. ` +
                'Adding items from a different restaurant will clear your current cart. Continue?'
            );
            
            if (!confirmSwitch) {
                return false;
            }
            
            this.clearCart();
        }

        // Set restaurant info if cart is empty
        if (!this.cart.restaurantId) {
            this.cart.restaurantId = menuItem.restaurantId;
            this.cart.restaurantName = menuItem.restaurantName;
            this.cart.deliveryFee = menuItem.deliveryFee || 30;
        }

        // Check if item already exists in cart
        const existingItemIndex = this.cart.items.findIndex(
            item => item.id === menuItem.id && item.specialInstructions === specialInstructions
        );

        if (existingItemIndex > -1) {
            // Update quantity of existing item
            this.cart.items[existingItemIndex].quantity += quantity;
        } else {
            // Add new item to cart
            const cartItem = {
                id: menuItem.id,
                name: menuItem.name,
                description: menuItem.description,
                price: menuItem.price,
                imageUrl: menuItem.imageUrl,
                quantity: quantity,
                specialInstructions: specialInstructions,
                isVegetarian: menuItem.isVegetarian,
                isVegan: menuItem.isVegan,
                isSpicy: menuItem.isSpicy
            };
            
            this.cart.items.push(cartItem);
        }

        this.calculateTotals();
        this.saveCartToStorage();
        this.updateCartUI();
        
        Utils.showToast(`${menuItem.name} added to cart!`, 'success');
        return true;
    }

    /**
     * Remove item from cart
     */
    removeItem(itemId, specialInstructions = '') {
        const itemIndex = this.cart.items.findIndex(
            item => item.id === itemId && item.specialInstructions === specialInstructions
        );

        if (itemIndex > -1) {
            const item = this.cart.items[itemIndex];
            this.cart.items.splice(itemIndex, 1);
            
            // Clear restaurant info if cart is empty
            if (this.cart.items.length === 0) {
                this.cart.restaurantId = null;
                this.cart.restaurantName = '';
                this.cart.deliveryFee = 0;
            }

            this.calculateTotals();
            this.saveCartToStorage();
            this.updateCartUI();
            
            Utils.showToast(`${item.name} removed from cart`, 'info');
        }
    }

    /**
     * Update item quantity
     */
    updateQuantity(itemId, newQuantity, specialInstructions = '') {
        const itemIndex = this.cart.items.findIndex(
            item => item.id === itemId && item.specialInstructions === specialInstructions
        );

        if (itemIndex > -1) {
            if (newQuantity <= 0) {
                this.removeItem(itemId, specialInstructions);
            } else {
                this.cart.items[itemIndex].quantity = newQuantity;
                this.calculateTotals();
                this.saveCartToStorage();
                this.updateCartUI();
            }
        }
    }

    /**
     * Clear entire cart
     */
    clearCart() {
        this.cart = {
            items: [],
            restaurantId: null,
            restaurantName: '',
            subtotal: 0,
            deliveryFee: 0,
            tax: 0,
            total: 0
        };
        
        this.saveCartToStorage();
        this.updateCartUI();
        Utils.showToast('Cart cleared', 'info');
    }

    /**
     * Calculate cart totals
     */
    calculateTotals() {
        this.cart.subtotal = this.cart.items.reduce((total, item) => {
            return total + (item.price * item.quantity);
        }, 0);

        // Calculate tax (5% of subtotal)
        this.cart.tax = this.cart.subtotal * 0.05;

        // Calculate total
        this.cart.total = this.cart.subtotal + this.cart.deliveryFee + this.cart.tax;
    }

    /**
     * Get cart data
     */
    getCart() {
        return { ...this.cart };
    }

    /**
     * Get cart item count
     */
    getItemCount() {
        return this.cart.items.reduce((total, item) => total + item.quantity, 0);
    }

    /**
     * Check if cart is empty
     */
    isEmpty() {
        return this.cart.items.length === 0;
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Cart button
        const cartBtn = document.getElementById('cartBtn');
        if (cartBtn) {
            cartBtn.addEventListener('click', () => {
                this.toggleCartSidebar();
            });
        }

        // Close cart button
        const closeCart = document.getElementById('closeCart');
        if (closeCart) {
            closeCart.addEventListener('click', () => {
                this.hideCartSidebar();
            });
        }

        // Cart overlay
        const cartOverlay = document.getElementById('cartOverlay');
        if (cartOverlay) {
            cartOverlay.addEventListener('click', () => {
                this.hideCartSidebar();
            });
        }

        // Checkout button
        const checkoutBtn = document.getElementById('checkoutBtn');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', () => {
                this.proceedToCheckout();
            });
        }
    }

    /**
     * Update cart UI
     */
    updateCartUI() {
        this.updateCartCount();
        this.updateCartItems();
        this.updateCartTotal();
        this.updateCheckoutButton();
    }

    /**
     * Update cart count badge
     */
    updateCartCount() {
        const cartCount = document.getElementById('cartCount');
        const itemCount = this.getItemCount();
        
        if (cartCount) {
            if (itemCount > 0) {
                cartCount.textContent = itemCount;
                cartCount.classList.remove('hidden');
            } else {
                cartCount.classList.add('hidden');
            }
        }
    }

    /**
     * Update cart items display
     */
    updateCartItems() {
        const cartItems = document.getElementById('cartItems');
        if (!cartItems) return;

        if (this.isEmpty()) {
            cartItems.innerHTML = `
                <div class="text-center py-8">
                    <i class="fas fa-shopping-cart text-4xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500">Your cart is empty</p>
                    <p class="text-sm text-gray-400 mt-2">Add some delicious items to get started!</p>
                </div>
            `;
            return;
        }

        let itemsHTML = '';
        
        // Restaurant info
        if (this.cart.restaurantName) {
            itemsHTML += `
                <div class="mb-4 p-3 bg-gray-50 rounded-lg">
                    <div class="flex items-center">
                        <i class="fas fa-store text-primary mr-2"></i>
                        <span class="font-medium">${this.cart.restaurantName}</span>
                    </div>
                </div>
            `;
        }

        // Cart items
        this.cart.items.forEach(item => {
            itemsHTML += `
                <div class="cart-item mb-4 p-3 border rounded-lg">
                    <div class="flex items-start justify-between">
                        <div class="flex-1">
                            <h4 class="font-medium text-gray-900">${item.name}</h4>
                            ${item.specialInstructions ? `<p class="text-xs text-gray-500 mt-1">Note: ${item.specialInstructions}</p>` : ''}
                            <div class="flex items-center mt-2">
                                ${item.isVegetarian ? '<span class="text-xs bg-green-100 text-green-800 px-2 py-1 rounded mr-1">Veg</span>' : ''}
                                ${item.isSpicy ? '<span class="text-xs bg-red-100 text-red-800 px-2 py-1 rounded mr-1">Spicy</span>' : ''}
                            </div>
                            <div class="flex items-center justify-between mt-3">
                                <span class="font-semibold text-primary">${Utils.formatCurrency(item.price)}</span>
                                <div class="flex items-center">
                                    <button class="quantity-btn minus w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center" 
                                            data-item-id="${item.id}" data-instructions="${item.specialInstructions}">
                                        <i class="fas fa-minus text-xs"></i>
                                    </button>
                                    <span class="mx-3 font-medium">${item.quantity}</span>
                                    <button class="quantity-btn plus w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center" 
                                            data-item-id="${item.id}" data-instructions="${item.specialInstructions}">
                                        <i class="fas fa-plus text-xs"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <button class="remove-item ml-3 text-gray-400 hover:text-red-500" 
                                data-item-id="${item.id}" data-instructions="${item.specialInstructions}">
                            <i class="fas fa-trash text-sm"></i>
                        </button>
                    </div>
                </div>
            `;
        });

        // Order summary
        itemsHTML += `
            <div class="border-t pt-4 mt-4">
                <div class="space-y-2 text-sm">
                    <div class="flex justify-between">
                        <span>Subtotal</span>
                        <span>${Utils.formatCurrency(this.cart.subtotal)}</span>
                    </div>
                    <div class="flex justify-between">
                        <span>Delivery Fee</span>
                        <span>${Utils.formatCurrency(this.cart.deliveryFee)}</span>
                    </div>
                    <div class="flex justify-between">
                        <span>Tax</span>
                        <span>${Utils.formatCurrency(this.cart.tax)}</span>
                    </div>
                </div>
            </div>
        `;

        cartItems.innerHTML = itemsHTML;

        // Add event listeners for quantity buttons and remove buttons
        this.setupCartItemListeners();
    }

    /**
     * Setup event listeners for cart items
     */
    setupCartItemListeners() {
        // Quantity buttons
        document.querySelectorAll('.quantity-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const itemId = parseInt(e.currentTarget.dataset.itemId);
                const instructions = e.currentTarget.dataset.instructions;
                const isPlus = e.currentTarget.classList.contains('plus');
                
                const currentItem = this.cart.items.find(
                    item => item.id === itemId && item.specialInstructions === instructions
                );
                
                if (currentItem) {
                    const newQuantity = isPlus ? currentItem.quantity + 1 : currentItem.quantity - 1;
                    this.updateQuantity(itemId, newQuantity, instructions);
                }
            });
        });

        // Remove buttons
        document.querySelectorAll('.remove-item').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const itemId = parseInt(e.currentTarget.dataset.itemId);
                const instructions = e.currentTarget.dataset.instructions;
                this.removeItem(itemId, instructions);
            });
        });
    }

    /**
     * Update cart total display
     */
    updateCartTotal() {
        const cartTotal = document.getElementById('cartTotal');
        if (cartTotal) {
            cartTotal.textContent = Utils.formatCurrency(this.cart.total);
        }
    }

    /**
     * Update checkout button state
     */
    updateCheckoutButton() {
        const checkoutBtn = document.getElementById('checkoutBtn');
        if (checkoutBtn) {
            if (this.isEmpty()) {
                checkoutBtn.disabled = true;
                checkoutBtn.textContent = 'Cart is Empty';
            } else {
                checkoutBtn.disabled = false;
                checkoutBtn.textContent = 'Proceed to Checkout';
            }
        }
    }

    /**
     * Toggle cart sidebar
     */
    toggleCartSidebar() {
        const cartSidebar = document.getElementById('cartSidebar');
        const cartOverlay = document.getElementById('cartOverlay');
        
        if (cartSidebar && cartOverlay) {
            const isVisible = !cartSidebar.classList.contains('translate-x-full');
            
            if (isVisible) {
                this.hideCartSidebar();
            } else {
                this.showCartSidebar();
            }
        }
    }

    /**
     * Show cart sidebar
     */
    showCartSidebar() {
        const cartSidebar = document.getElementById('cartSidebar');
        const cartOverlay = document.getElementById('cartOverlay');
        
        if (cartSidebar && cartOverlay) {
            cartSidebar.classList.remove('translate-x-full');
            cartOverlay.classList.remove('hidden');
            document.body.style.overflow = 'hidden';
        }
    }

    /**
     * Hide cart sidebar
     */
    hideCartSidebar() {
        const cartSidebar = document.getElementById('cartSidebar');
        const cartOverlay = document.getElementById('cartOverlay');
        
        if (cartSidebar && cartOverlay) {
            cartSidebar.classList.add('translate-x-full');
            cartOverlay.classList.add('hidden');
            document.body.style.overflow = '';
        }
    }

    /**
     * Proceed to checkout
     */
    proceedToCheckout() {
        if (this.isEmpty()) {
            Utils.showToast('Your cart is empty', 'warning');
            return;
        }

        if (!authManager.isAuthenticated()) {
            Utils.showToast('Please login to continue', 'warning');
            window.location.href = 'pages/login.html';
            return;
        }

        // Redirect to checkout page
        window.location.href = 'pages/checkout.html';
    }
}

// Create global cart manager instance
const cartManager = new CartManager();