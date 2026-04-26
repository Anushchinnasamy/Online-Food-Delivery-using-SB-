# FoodExpress - Online Food Delivery Frontend

A modern, production-ready React frontend for the online food delivery application built with Vite, Tailwind CSS, and Redux Toolkit.

## 🚀 Features

### Customer Features
- Browse restaurants with search and filters (city, cuisine)
- View restaurant details and menu items
- Add items to cart with quantity management
- Place orders with multiple payment methods
- Track order status in real-time
- View order history
- Cancel orders (when allowed)

### Restaurant Admin Features
- Restaurant dashboard with analytics
- Manage menu items (CRUD operations)
- View and manage orders
- Update order status
- Toggle menu item availability

### Platform Admin Features
- View all orders by status
- Approve restaurants
- Platform-wide analytics

## 🛠️ Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **Redux Toolkit** - State management
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **React Hot Toast** - Toast notifications

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable components
│   │   ├── Navbar.jsx
│   │   ├── LoadingSpinner.jsx
│   │   ├── RestaurantCard.jsx
│   │   ├── MenuItemCard.jsx
│   │   └── ProtectedRoute.jsx
│   ├── pages/              # Page components
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── RestaurantDetails.jsx
│   │   ├── Cart.jsx
│   │   ├── Checkout.jsx
│   │   ├── Orders.jsx
│   │   ├── OrderDetails.jsx
│   │   └── restaurant/     # Restaurant admin pages
│   │       └── RestaurantDashboard.jsx
│   ├── services/           # API service layer
│   │   ├── authService.js
│   │   ├── restaurantService.js
│   │   ├── menuService.js
│   │   └── orderService.js
│   ├── store/              # Redux store
│   │   ├── store.js
│   │   └── slices/
│   │       ├── authSlice.js
│   │       ├── cartSlice.js
│   │       └── restaurantSlice.js
│   ├── utils/              # Utility functions
│   │   ├── constants.js
│   │   └── helpers.js
│   ├── config/             # Configuration
│   │   └── api.js
│   ├── App.jsx             # Main app component
│   ├── main.jsx            # Entry point
│   └── index.css           # Global styles
├── public/                 # Static assets
├── .env                    # Environment variables
├── .env.example            # Environment variables example
├── index.html              # HTML template
├── package.json            # Dependencies
├── tailwind.config.js      # Tailwind configuration
├── postcss.config.js       # PostCSS configuration
└── vite.config.js          # Vite configuration
```

## 🚦 Getting Started

### Prerequisites

- Node.js 16+ and npm
- Backend API running on `http://localhost:8080` (or update `.env`)

### Installation

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Configure environment variables:**
   ```bash
   cp .env.example .env
   ```
   
   Update `.env` with your backend API URL:
   ```
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```
   
   The app will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
```

The production build will be in the `dist/` directory.

### Preview Production Build

```bash
npm run preview
```

## 🔐 Authentication

The app uses JWT-based authentication. Tokens are stored in `localStorage` and automatically attached to API requests via Axios interceptors.

### Demo Credentials

**Customer Account:**
- Email: `customer@test.com`
- Password: `password123`

**Restaurant Admin:**
- Email: `restaurant@test.com`
- Password: `password123`

**Platform Admin:**
- Email: `admin@test.com`
- Password: `password123`

## 🎨 UI/UX Design

The frontend follows a Swiggy-inspired design with:
- Clean, modern interface
- Card-based layouts
- Bold typography
- Responsive design (mobile-first)
- Smooth transitions and hover effects
- Loading states and skeleton UI
- Toast notifications for user feedback

### Color Scheme

- Primary: Orange (#f97316)
- Success: Green
- Error: Red
- Warning: Yellow
- Info: Blue

## 📱 Responsive Design

The application is fully responsive and works seamlessly on:
- Mobile devices (320px+)
- Tablets (768px+)
- Desktops (1024px+)
- Large screens (1280px+)

## 🔄 State Management

### Redux Store Structure

```javascript
{
  auth: {
    user: {...},
    isAuthenticated: boolean,
    loading: boolean,
    error: string
  },
  cart: {
    items: [...],
    restaurant: {...}
  },
  restaurant: {
    restaurants: [...],
    currentRestaurant: {...},
    loading: boolean,
    error: string
  }
}
```

### Cart Persistence

Cart data is automatically persisted to `localStorage` and restored on page reload.

## 🌐 API Integration

All API calls are centralized in service files:

- `authService.js` - Authentication endpoints
- `restaurantService.js` - Restaurant CRUD operations
- `menuService.js` - Menu item operations
- `orderService.js` - Order management

### API Configuration

The Axios instance is configured with:
- Base URL from environment variables
- Automatic JWT token attachment
- Response/request interceptors
- Error handling
- Automatic redirect on 401 (unauthorized)

## 🛡️ Protected Routes

Routes are protected based on user roles:

- **Public:** Home, Login, Register, Restaurant Details
- **Customer:** Cart, Checkout, Orders
- **Restaurant Admin:** Dashboard, Menu Management, Order Management
- **Platform Admin:** Admin Dashboard

## 🎯 Key Features Implementation

### Search & Filters
- Search restaurants by name
- Filter by city
- Filter by cuisine type
- Real-time search results

### Cart Management
- Add/remove items
- Update quantities
- Clear cart
- Restaurant validation (can't mix items from different restaurants)
- Persistent cart across sessions

### Order Tracking
- Real-time order status
- Status timeline visualization
- Order history
- Cancel orders (when allowed)

### Restaurant Dashboard
- Order statistics
- Revenue tracking
- Active orders count
- Quick action links

## 🐛 Error Handling

- Global error handling via Axios interceptors
- User-friendly error messages
- Toast notifications for errors
- Fallback UI for failed API calls
- Loading states for async operations

## 🔧 Configuration

### Tailwind CSS

Custom configuration includes:
- Extended color palette
- Custom components (buttons, cards, badges)
- Responsive breakpoints
- Custom animations

### Vite

Optimized for:
- Fast HMR (Hot Module Replacement)
- Optimized production builds
- Code splitting
- Asset optimization

## 📝 Backend API Compatibility

This frontend is designed to work with the Spring Boot backend with the following endpoints:

### Public Endpoints
- `GET /restaurants` - Get all restaurants
- `GET /restaurants/{id}` - Get restaurant details
- `GET /restaurants/search` - Search restaurants
- `GET /menu-items/restaurant/{id}` - Get restaurant menu
- `POST /auth/login` - User login
- `POST /auth/register` - User registration

### Protected Endpoints (Require Authentication)
- `POST /orders` - Create order
- `GET /orders/my` - Get user orders
- `GET /orders/{id}` - Get order details
- `PUT /orders/{id}/cancel` - Cancel order
- `GET /restaurants/my` - Get restaurant (admin)
- `POST /menu-items` - Create menu item (admin)
- `PUT /menu-items/{id}` - Update menu item (admin)
- `DELETE /menu-items/{id}` - Delete menu item (admin)

## 🚨 Known Limitations

1. **Image Upload:** Currently uses image URLs. File upload not implemented.
2. **Real-time Updates:** Order status updates require manual refresh.
3. **Payment Gateway:** Payment methods are UI-only, no actual payment processing.
4. **Geolocation:** Location features use manual address input.

## 🔮 Future Enhancements

- [ ] Real-time order tracking with WebSockets
- [ ] Image upload for restaurants and menu items
- [ ] Payment gateway integration
- [ ] Google Maps integration
- [ ] Push notifications
- [ ] Reviews and ratings
- [ ] Favorites/Wishlist
- [ ] Order scheduling
- [ ] Promo codes and discounts
- [ ] Multi-language support

## 📄 License

This project is part of the Online Food Delivery application.

## 🤝 Contributing

1. Follow the existing code structure
2. Use Tailwind CSS for styling
3. Maintain responsive design
4. Add proper error handling
5. Test on multiple devices

## 📞 Support

For issues or questions, please refer to the main project documentation.

---

Built with ❤️ using React, Vite, and Tailwind CSS
