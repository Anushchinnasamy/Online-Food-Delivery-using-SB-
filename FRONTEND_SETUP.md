# Frontend Setup Guide

## Quick Start

### 1. Navigate to Frontend Directory
```bash
cd "Online Food Delivery/frontend"
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Configure Environment
Create a `.env` file (already created):
```env
VITE_API_BASE_URL=http://localhost:8080
```

### 4. Start Development Server
```bash
npm run dev
```

The application will be available at: **http://localhost:5173**

## Backend Requirements

**IMPORTANT:** The backend must be running before using the frontend.

### Start Backend
```bash
cd "Online Food Delivery/backend"
mvn spring-boot:run
```

Backend will run on: **http://localhost:8080**

## Testing the Application

### 1. Login with Demo Accounts

**Customer Account:**
- Email: `customer@test.com`
- Password: `password123`
- Can: Browse restaurants, add to cart, place orders

**Restaurant Admin:**
- Email: `restaurant@test.com`
- Password: `password123`
- Can: Manage restaurant, menu items, and orders

**Platform Admin:**
- Email: `admin@test.com`
- Password: `password123`
- Can: Approve restaurants, view all orders

### 2. Customer Flow

1. **Browse Restaurants**
   - Visit home page
   - Use search and filters
   - Click on a restaurant card

2. **View Menu & Add to Cart**
   - Browse menu items
   - Click "Add to Cart"
   - View cart icon (top right)

3. **Checkout**
   - Click cart icon
   - Review items
   - Click "Proceed to Checkout"
   - Fill delivery details
   - Select payment method
   - Place order

4. **Track Orders**
   - Click "My Orders" in navbar
   - View order status
   - Click order for details
   - Cancel if needed

### 3. Restaurant Admin Flow

1. **Dashboard**
   - View statistics
   - See active orders
   - Check revenue

2. **Manage Menu**
   - Add new items
   - Edit existing items
   - Toggle availability
   - Delete items

3. **Manage Orders**
   - View incoming orders
   - Update order status
   - Process orders

## Project Structure

```
frontend/
├── src/
│   ├── components/       # Reusable UI components
│   ├── pages/           # Page components
│   ├── services/        # API integration
│   ├── store/           # Redux state management
│   ├── utils/           # Helper functions
│   └── config/          # Configuration
├── public/              # Static assets
└── package.json         # Dependencies
```

## Available Scripts

```bash
# Development server with HMR
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

## Key Features Implemented

### ✅ Customer Features
- [x] Browse restaurants with search/filters
- [x] View restaurant details and menu
- [x] Shopping cart with persistence
- [x] Place orders
- [x] Track order status
- [x] Order history
- [x] Cancel orders

### ✅ Restaurant Admin Features
- [x] Dashboard with analytics
- [x] Menu management (CRUD)
- [x] Order management
- [x] Status updates

### ✅ UI/UX Features
- [x] Responsive design (mobile-first)
- [x] Loading states
- [x] Error handling
- [x] Toast notifications
- [x] Protected routes
- [x] Role-based access

## Technology Stack

| Technology | Purpose |
|------------|---------|
| React 18 | UI Framework |
| Vite | Build Tool |
| Tailwind CSS | Styling |
| Redux Toolkit | State Management |
| React Router | Routing |
| Axios | HTTP Client |
| React Hot Toast | Notifications |

## API Integration

### Base URL
```javascript
http://localhost:8080
```

### Authentication
- JWT tokens stored in localStorage
- Automatically attached to requests
- Auto-redirect on 401 errors

### Endpoints Used

**Public:**
- `GET /restaurants`
- `GET /restaurants/{id}`
- `GET /restaurants/search`
- `GET /menu-items/restaurant/{id}`
- `POST /auth/login`
- `POST /auth/register`

**Protected:**
- `POST /orders`
- `GET /orders/my`
- `GET /orders/{id}`
- `PUT /orders/{id}/cancel`
- `GET /restaurants/my`
- `POST /menu-items`
- `PUT /menu-items/{id}`
- `DELETE /menu-items/{id}`

## Troubleshooting

### Issue: Cannot connect to backend
**Solution:** Ensure backend is running on port 8080
```bash
cd backend
mvn spring-boot:run
```

### Issue: CORS errors
**Solution:** Backend has `@CrossOrigin` enabled. Check backend logs.

### Issue: Login fails
**Solution:** 
1. Check backend is running
2. Verify demo accounts exist in database
3. Check browser console for errors

### Issue: Images not loading
**Solution:** Using Unsplash CDN images. Check internet connection.

### Issue: Cart not persisting
**Solution:** Check browser localStorage is enabled

### Issue: Build fails
**Solution:**
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## Environment Variables

```env
# Backend API URL
VITE_API_BASE_URL=http://localhost:8080

# For production, update to your deployed backend URL
# VITE_API_BASE_URL=https://api.yourapp.com
```

## Production Deployment

### Build
```bash
npm run build
```

### Deploy
The `dist/` folder contains the production build. Deploy to:
- Vercel
- Netlify
- AWS S3 + CloudFront
- Any static hosting service

### Environment Configuration
Update `.env` with production backend URL before building.

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
- Mobile browsers

## Performance

- Code splitting enabled
- Lazy loading for routes
- Optimized images
- Minified production build
- Gzip compression ready

## Security

- JWT token authentication
- Protected routes
- Role-based access control
- XSS protection (React default)
- CSRF protection via tokens

## Backend API Compatibility Notes

### ✅ Fully Compatible
All backend endpoints are properly integrated without modifications.

### 📝 Data Flow
1. User logs in → JWT token stored
2. Token attached to all requests
3. Backend validates token
4. Returns user-specific data

### 🔄 Order Status Flow
```
PLACED → ACCEPTED → PREPARING → READY_FOR_PICKUP → 
PICKED_UP → OUT_FOR_DELIVERY → DELIVERED
```

### 💳 Payment Methods
- Cash on Delivery
- Credit Card
- Debit Card
- UPI
- Net Banking
- Digital Wallet

## Additional Resources

- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [Tailwind CSS Documentation](https://tailwindcss.com)
- [Redux Toolkit Documentation](https://redux-toolkit.js.org)

## Support

For issues or questions:
1. Check browser console for errors
2. Check backend logs
3. Verify API endpoints are accessible
4. Check network tab in browser DevTools

---

**Happy Coding! 🚀**
