# FoodDelivery Platform

A complete, production-ready online food ordering platform similar to Swiggy/Zomato.

## System Architecture

### Tech Stack
- **Backend**: Spring Boot, PostgreSQL, JWT Authentication
- **Frontend**: HTML, Tailwind CSS, Vanilla JavaScript
- **Security**: Role-based authorization, password hashing

### User Roles
1. **CUSTOMER** - Browse restaurants, place orders, track deliveries
2. **RESTAURANT_ADMIN** - Manage menus, process orders, view reports
3. **DELIVERY_PARTNER** - Accept deliveries, update status, track earnings
4. **PLATFORM_ADMIN** - Manage platform, users, analytics

## Project Structure

```
food-delivery-platform/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/fooddelivery/
│   │       ├── config/      # Security, database config
│   │       ├── controller/  # REST controllers
│   │       ├── dto/         # Data transfer objects
│   │       ├── entity/      # JPA entities
│   │       ├── repository/  # Data repositories
│   │       ├── service/     # Business logic
│   │       └── util/        # Utilities
│   └── src/main/resources/
│       ├── application.yml
│       └── data.sql
├── frontend/               # Web application
│   ├── assets/            # CSS, JS, images
│   ├── pages/             # HTML pages
│   └── js/                # JavaScript modules
└── docs/                  # Documentation
```

## Features

### Customer Features
- Location-based restaurant discovery
- Menu browsing with categories
- Shopping cart management
- Order placement and tracking
- Payment simulation
- Order history and reordering

### Restaurant Admin Features
- Menu management (CRUD)
- Order processing workflow
- Sales reports and analytics
- Restaurant profile management

### Delivery Partner Features
- Order assignment and acceptance
- Delivery status updates
- Earnings tracking
- Delivery history

### Platform Admin Features
- User and restaurant management
- Platform-wide analytics
- System monitoring and controls

## Getting Started

1. **Backend Setup**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Frontend Setup**
   ```bash
   cd frontend
   # Serve with any HTTP server
   python -m http.server 8080
   ```

3. **Database Setup**
   - PostgreSQL required
   - Schema auto-created on startup
   - Sample data loaded from data.sql

## API Documentation

Base URL: `http://localhost:8080/api`

### Authentication Endpoints
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh` - Token refresh

### Customer Endpoints
- `GET /restaurants` - List restaurants
- `GET /restaurants/{id}/menu` - Restaurant menu
- `POST /orders` - Place order
- `GET /orders/my` - Order history

### Restaurant Admin Endpoints
- `GET /restaurant/orders` - Incoming orders
- `PUT /restaurant/orders/{id}/status` - Update order status
- `POST /restaurant/menu` - Add menu item

### Delivery Partner Endpoints
- `GET /delivery/available` - Available deliveries
- `PUT /delivery/{id}/accept` - Accept delivery
- `PUT /delivery/{id}/status` - Update delivery status

## Security

- JWT-based authentication
- Role-based authorization
- Password hashing with BCrypt
- CORS configuration
- Input validation and sanitization

## Database Schema

Key entities:
- Users (with roles)
- Restaurants
- MenuItems
- Orders & OrderItems
- Payments
- Deliveries

All tables include audit fields and soft delete support.