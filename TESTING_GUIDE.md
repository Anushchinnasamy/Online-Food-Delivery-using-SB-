# 🧪 Complete Testing Guide

## ✅ Servers Status

### Backend (Spring Boot)
- **Status:** ✅ Running
- **URL:** http://localhost:8081
- **Terminal ID:** 19

### Frontend (React + Vite)
- **Status:** ✅ Running  
- **URL:** http://localhost:5173
- **Terminal ID:** 20

---

## 🌐 Access the Application

### **Frontend URL:** 
# **http://localhost:5173**

---

## 🔐 Working Test Credentials

### Customer Accounts
```
Email: john.doe@gmail.com
Password: password123

Email: jane.smith@gmail.com
Password: password123

Email: mike.johnson@gmail.com
Password: password123
```

### Restaurant Admin Accounts
```
Email: raj@spicepalace.com
Password: password123

Email: priya@pizzacorner.com
Password: password123

Email: ahmed@burgerking.com
Password: password123
```

### Platform Admin Account
```
Email: admin@fooddelivery.com
Password: password123
```

### Delivery Partner Accounts
```
Email: ravi.delivery@gmail.com
Password: password123

Email: suresh.delivery@gmail.com
Password: password123

Email: vikram.delivery@gmail.com
Password: password123
```

---

## 🧪 Test Scenarios

### Test 1: Register New Customer
1. Go to http://localhost:5173
2. Click "Sign Up" button
3. Fill in the registration form:
   - Full Name: Your Name
   - Email: yourname@test.com
   - Phone: 9876543210
   - Password: test123456
   - Confirm Password: test123456
   - Role: Customer
   - Address: (optional)
   - City: (optional)
4. Click "Create Account"
5. ✅ Should redirect to home page with logged-in state

### Test 2: Login as Customer
1. Go to http://localhost:5173/login
2. Enter credentials:
   - Email: john.doe@gmail.com
   - Password: password123
3. Click "Sign In"
4. ✅ Should redirect to home page
5. ✅ Should see "John Doe" in navbar
6. ✅ Should see cart icon

### Test 3: Browse Restaurants (Public)
1. Go to http://localhost:5173
2. ✅ Should see list of restaurants
3. Try search: Type "Spice" in search bar
4. Click "Search"
5. ✅ Should filter restaurants
6. Try city filter: Select a city from dropdown
7. ✅ Should filter by city
8. Try cuisine filter: Select a cuisine
9. ✅ Should filter by cuisine

### Test 4: View Restaurant Details
1. From home page, click on any restaurant card
2. ✅ Should navigate to restaurant details page
3. ✅ Should see restaurant info (name, rating, address)
4. ✅ Should see menu items
5. Try category filter: Click on different categories
6. ✅ Should filter menu items by category

### Test 5: Add Items to Cart (Customer)
1. Login as customer (john.doe@gmail.com)
2. Go to any restaurant
3. Click "Add to Cart" on a menu item
4. ✅ Should show success toast "Added to cart!"
5. ✅ Cart icon should show item count (1)
6. Add another item
7. ✅ Cart count should increase (2)
8. Try adding item from different restaurant
9. ✅ Should clear cart and add new item

### Test 6: View and Manage Cart
1. Click cart icon in navbar
2. ✅ Should navigate to cart page
3. ✅ Should see all cart items
4. ✅ Should see restaurant info
5. ✅ Should see order summary (subtotal, delivery fee, tax, total)
6. Click "+" to increase quantity
7. ✅ Quantity should increase
8. ✅ Total should update
9. Click "-" to decrease quantity
10. ✅ Quantity should decrease
11. Click "X" to remove item
12. ✅ Item should be removed

### Test 7: Checkout and Place Order
1. From cart page, click "Proceed to Checkout"
2. ✅ Should navigate to checkout page
3. Fill in delivery details:
   - Delivery Address: 123 Main St, Apartment 4B
   - Phone: 9876543210
   - Special Instructions: Ring the doorbell
4. Select payment method (e.g., Cash on Delivery)
5. Click "Place Order"
6. ✅ Should show success
7. ✅ Should navigate to order details page
8. ✅ Cart should be cleared

### Test 8: View Order History
1. Login as customer
2. Click "My Orders" in navbar
3. ✅ Should see list of orders
4. ✅ Each order should show:
   - Restaurant name
   - Order number
   - Date
   - Status badge
   - Total amount
   - Items summary
5. Click on an order
6. ✅ Should navigate to order details

### Test 9: View Order Details and Track Status
1. From orders page, click on any order
2. ✅ Should see order details page
3. ✅ Should see status timeline
4. ✅ Should see restaurant info
5. ✅ Should see order items with quantities
6. ✅ Should see delivery details
7. ✅ Should see payment summary
8. If order is PLACED or ACCEPTED:
   - ✅ Should see "Cancel Order" button
   - Click "Cancel Order"
   - Confirm cancellation
   - ✅ Status should change to CANCELLED

### Test 10: Login as Restaurant Admin
1. Logout (if logged in)
2. Go to login page
3. Enter credentials:
   - Email: raj@spicepalace.com
   - Password: password123
4. Click "Sign In"
5. ✅ Should redirect to restaurant dashboard
6. ✅ Should see different navbar (Dashboard, Orders, Menu)

### Test 11: Restaurant Dashboard
1. Login as restaurant admin
2. ✅ Should see restaurant info card
3. ✅ Should see statistics:
   - Active Orders count
   - Today's Orders count
   - Today's Revenue
   - Total Orders count
4. ✅ Should see quick action cards:
   - Manage Orders
   - Manage Menu
   - View Restaurant

### Test 12: View Restaurant Orders
1. From dashboard, click "Manage Orders"
2. ✅ Should see list of orders for the restaurant
3. ✅ Each order should show status
4. Click on an order
5. ✅ Should see order details
6. Try updating order status (if applicable)

### Test 13: Manage Menu Items
1. From dashboard, click "Manage Menu"
2. ✅ Should see list of menu items
3. Try adding new item (if implemented)
4. Try editing existing item (if implemented)
5. Try toggling availability (if implemented)

### Test 14: Login as Platform Admin
1. Logout
2. Login with:
   - Email: admin@fooddelivery.com
   - Password: password123
3. ✅ Should redirect to admin dashboard
4. ✅ Should see admin-specific features

### Test 15: Logout
1. Click "Logout" button in navbar
2. ✅ Should redirect to home page
3. ✅ Should clear user session
4. ✅ Navbar should show "Login" and "Sign Up" buttons

### Test 16: Protected Routes
1. Logout (if logged in)
2. Try to access: http://localhost:5173/cart
3. ✅ Should redirect to login page
4. Try to access: http://localhost:5173/orders
5. ✅ Should redirect to login page
6. Try to access: http://localhost:5173/restaurant/dashboard
7. ✅ Should redirect to login page

### Test 17: Role-Based Access
1. Login as customer
2. Try to access: http://localhost:5173/restaurant/dashboard
3. ✅ Should redirect to home page (not authorized)
4. Logout and login as restaurant admin
5. Try to access: http://localhost:5173/cart
6. ✅ Should redirect to home page (not authorized)

### Test 18: Responsive Design
1. Open browser DevTools (F12)
2. Toggle device toolbar (Ctrl+Shift+M)
3. Test on different screen sizes:
   - Mobile (375px)
   - Tablet (768px)
   - Desktop (1024px)
4. ✅ Layout should adapt properly
5. ✅ All features should work on mobile

### Test 19: Error Handling
1. Turn off backend (stop the Spring Boot server)
2. Try to login
3. ✅ Should show error message
4. Try to browse restaurants
5. ✅ Should show error message with "Try Again" button
6. Start backend again
7. Click "Try Again"
8. ✅ Should load data successfully

### Test 20: Cart Persistence
1. Login as customer
2. Add items to cart
3. Refresh the page (F5)
4. ✅ Cart should still have items
5. Close browser
6. Open browser again
7. Go to http://localhost:5173
8. ✅ Cart should still have items (if not logged out)

---

## 🐛 Common Issues and Solutions

### Issue: "Cannot connect to backend"
**Solution:** 
```bash
# Check if backend is running
# Terminal should show: "Food Delivery Platform Started Successfully!"
# If not, restart backend:
cd "Online Food Delivery/backend"
mvn spring-boot:run
```

### Issue: "Login fails with correct credentials"
**Solution:**
- Check backend logs for errors
- Verify you're using the correct email (not the old test credentials)
- Clear browser localStorage and try again

### Issue: "Restaurants not loading"
**Solution:**
- Check browser console (F12) for errors
- Verify backend is running on port 8081
- Check network tab to see API calls

### Issue: "Cart not working"
**Solution:**
- Make sure you're logged in as CUSTOMER
- Check browser localStorage is enabled
- Clear cart and try again

### Issue: "Images not loading"
**Solution:**
- Check internet connection (uses Unsplash CDN)
- Images will fallback to default if URL fails

### Issue: "CORS errors"
**Solution:**
- Backend has CORS enabled
- Check backend logs
- Verify frontend is calling http://localhost:8081

---

## 📊 Expected Results Summary

### ✅ Working Features
- [x] User registration (all roles)
- [x] User login (all roles)
- [x] Browse restaurants (public)
- [x] Search and filter restaurants
- [x] View restaurant details
- [x] View menu items
- [x] Add to cart (customer)
- [x] Update cart quantities
- [x] Remove from cart
- [x] Cart persistence
- [x] Checkout flow
- [x] Place orders
- [x] View order history
- [x] Track order status
- [x] Cancel orders
- [x] Restaurant dashboard
- [x] View restaurant orders
- [x] Protected routes
- [x] Role-based access
- [x] Responsive design
- [x] Error handling
- [x] Loading states
- [x] Toast notifications

---

## 🎯 Success Criteria

After testing, you should be able to:
1. ✅ Register new users
2. ✅ Login with different roles
3. ✅ Browse and search restaurants
4. ✅ Add items to cart
5. ✅ Place orders
6. ✅ Track orders
7. ✅ Access role-specific features
8. ✅ See proper error messages
9. ✅ Use on mobile devices

---

## 📝 Notes

- All API calls go to: http://localhost:8081
- Frontend runs on: http://localhost:5173
- JWT tokens are stored in localStorage
- Cart data is persisted in localStorage
- Demo credentials removed from login page
- Security configuration fixed for public endpoints

---

**Status:** ✅ ALL ISSUES FIXED - READY FOR TESTING

**Last Updated:** April 26, 2026 - 4:30 PM
