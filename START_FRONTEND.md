# 🚀 Quick Start Guide

## Start the Application

### Step 1: Start Backend (Terminal 1)
```bash
cd "Online Food Delivery/backend"
mvn spring-boot:run
```
✅ Backend will run on: **http://localhost:8080**

### Step 2: Start Frontend (Terminal 2)
```bash
cd "Online Food Delivery/frontend"
npm run dev
```
✅ Frontend will run on: **http://localhost:5173**

### Step 3: Open Browser
Navigate to: **http://localhost:5173**

## 🔐 Demo Login Credentials

### Customer Account
```
Email: customer@test.com
Password: password123
```
**Can do:** Browse restaurants, add to cart, place orders, track orders

### Restaurant Admin
```
Email: restaurant@test.com
Password: password123
```
**Can do:** Manage restaurant, menu items, and orders

### Platform Admin
```
Email: admin@test.com
Password: password123
```
**Can do:** Approve restaurants, view all orders

## 🎯 Quick Test Flow

### As Customer:
1. Login with customer credentials
2. Browse restaurants on home page
3. Click on a restaurant
4. Add items to cart
5. Click cart icon (top right)
6. Proceed to checkout
7. Fill delivery details
8. Place order
9. View order in "My Orders"

### As Restaurant Admin:
1. Login with restaurant admin credentials
2. View dashboard statistics
3. Click "Manage Menu"
4. Add/edit menu items
5. Click "Manage Orders"
6. Update order status

## 📁 Project Files

```
Online Food Delivery/
├── backend/                 # Spring Boot backend
├── frontend/               # React frontend
│   ├── src/
│   │   ├── components/    # UI components
│   │   ├── pages/         # Page components
│   │   ├── services/      # API services
│   │   ├── store/         # Redux store
│   │   └── utils/         # Helpers
│   ├── package.json
│   └── README.md
├── FRONTEND_SETUP.md       # Detailed setup guide
├── FRONTEND_COMPLETE.md    # Implementation summary
└── START_FRONTEND.md       # This file
```

## 🛠️ Useful Commands

### Frontend
```bash
# Install dependencies (first time only)
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Backend
```bash
# Run backend
mvn spring-boot:run

# Clean and rebuild
mvn clean install
```

## ⚠️ Troubleshooting

### "Cannot connect to backend"
- Ensure backend is running on port 8080
- Check backend terminal for errors

### "Port 5173 already in use"
- Stop other Vite processes
- Or change port in vite.config.js

### "Login fails"
- Verify backend is running
- Check browser console for errors
- Ensure demo accounts exist in database

### "Images not loading"
- Check internet connection (uses Unsplash CDN)
- Images will fallback to defaults

## 📚 Documentation

- **README.md** - Full project documentation
- **FRONTEND_SETUP.md** - Detailed setup instructions
- **FRONTEND_COMPLETE.md** - Implementation details

## 🎉 You're All Set!

The application is ready to use. Enjoy exploring the features!

---

**Need Help?**
- Check browser console for errors
- Review backend logs
- Read the documentation files
