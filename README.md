# 🍕 Food Delivery Platform

A complete full-stack online food ordering and delivery management system built with Spring Boot and modern web technologies.

## 🌟 Features

### For Customers
- 🔐 User registration and authentication with JWT
- 🍽️ Browse restaurants by city, cuisine, and ratings
- 📋 View detailed menus with dietary information
- 🛒 Place orders with multiple items
- 💳 Multiple payment methods support
- 📦 Real-time order tracking
- 📜 Order history and reordering

### For Restaurant Owners
- 🏪 Restaurant profile management
- 📝 Complete menu management (CRUD operations)
- 📊 Order management and status updates
- ⏰ Operating hours configuration
- 💰 Pricing and delivery fee settings
- 🔔 Real-time order notifications

### For Delivery Partners
- 📍 Delivery assignment and tracking
- 🚚 Route optimization
- ✅ Order pickup and delivery confirmation
- ⭐ Customer ratings and feedback

### For Platform Admins
- 👥 User management
- ✅ Restaurant approval workflow
- 📊 Platform analytics and reporting
- 🔧 System configuration

## 🛠️ Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Security:** Spring Security with JWT
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **API Documentation:** Swagger/OpenAPI 3.0
- **Build Tool:** Maven

### Key Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- PostgreSQL Driver
- JWT (jjwt 0.11.5)
- SpringDoc OpenAPI (2.3.0)

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/food-delivery-platform.git
   cd food-delivery-platform
   ```

2. **Configure Database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE fooddelivery;
   ```
   
   Update `backend/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/fooddelivery
       username: postgres
       password: your_password
   ```

3. **Build the project**
   ```bash
   cd backend
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API Base URL: `http://localhost:8081/api`
   - Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## 📚 API Documentation

### Interactive Documentation
Access the Swagger UI for interactive API testing:
```
http://localhost:8081/swagger-ui/index.html
```

### API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/auth/me` - Get current user

#### Restaurants
- `GET /api/restaurants` - List all restaurants
- `GET /api/restaurants/{id}` - Get restaurant details
- `GET /api/restaurants/search` - Search restaurants
- `POST /api/restaurants` - Create restaurant (Admin)
- `PUT /api/restaurants/{id}` - Update restaurant (Admin)

#### Menu Items
- `GET /api/menu-items/restaurant/{id}` - Get restaurant menu
- `GET /api/menu-items/search` - Search menu items
- `POST /api/menu-items` - Create menu item (Admin)
- `PUT /api/menu-items/{id}` - Update menu item (Admin)

#### Orders
- `POST /api/orders` - Place order
- `GET /api/orders/my` - Get customer orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status
- `PUT /api/orders/{id}/cancel` - Cancel order

For complete API documentation, see [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md)

## 🔐 Authentication

The application uses JWT (JSON Web Tokens) for authentication:

1. Register or login to receive an access token
2. Include the token in the Authorization header:
   ```
   Authorization: Bearer {your_token}
   ```
3. Tokens expire after 24 hours
4. Use refresh token to get a new access token

### Test Credentials

```
Customer:
  Email: john.doe@gmail.com
  Password: password123

Restaurant Admin:
  Email: raj@spicepalace.com
  Password: password123

Platform Admin:
  Email: admin@fooddelivery.com
  Password: password123
```

## 🏗️ Architecture

### Project Structure
```
backend/
├── src/main/java/com/fooddelivery/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA entities
│   ├── exception/      # Custom exceptions
│   ├── repository/     # Data repositories
│   ├── security/       # Security configuration
│   └── service/        # Business logic
└── src/main/resources/
    ├── application.yml  # Application configuration
    └── data.sql        # Sample data
```

### Database Schema

**Main Entities:**
- Users (multi-role: Customer, Restaurant Admin, Delivery Partner, Platform Admin)
- Restaurants
- Menu Items
- Orders
- Order Items
- Payments
- Deliveries

### Security Features
- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- CORS configuration
- Input validation
- SQL injection prevention

## 🧪 Testing

### Using Swagger UI
1. Start the application
2. Navigate to `http://localhost:8081/swagger-ui/index.html`
3. Use the "Authorize" button to add your JWT token
4. Test any endpoint interactively

### Using cURL
See [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md) for detailed cURL examples.

### Using Postman
Import the OpenAPI specification:
```
http://localhost:8081/v3/api-docs
```

## 📊 Sample Data

The application comes with pre-loaded sample data:
- 3 Restaurants (Indian, Italian, Fast Food)
- 15+ Menu Items
- 3 Delivery Partners
- 3 Customers
- 1 Platform Admin

## 🔧 Configuration

### Application Properties
Key configurations in `application.yml`:
- Server port: 8081
- Database connection
- JWT secret and expiration
- CORS settings
- Logging levels

### Environment Variables
For production, use environment variables:
```bash
export DB_URL=jdbc:postgresql://localhost:5433/fooddelivery
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key
```

## 🚀 Deployment

### Building for Production
```bash
mvn clean package -DskipTests
```

### Running the JAR
```bash
java -jar target/food-delivery-platform-1.0.0.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📈 Future Enhancements

- [ ] Real-time notifications (WebSocket)
- [ ] Payment gateway integration
- [ ] Reviews and ratings system
- [ ] Promo codes and discounts
- [ ] Analytics dashboard
- [ ] Mobile app support
- [ ] Multi-language support
- [ ] Advanced search filters

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

Your Name
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Spring Boot Documentation
- PostgreSQL Community
- Swagger/OpenAPI
- All contributors and supporters

---

⭐ Star this repository if you find it helpful!
