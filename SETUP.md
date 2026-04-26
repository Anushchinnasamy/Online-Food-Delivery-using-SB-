# Food Delivery Platform - Setup Guide

A complete, production-ready online food ordering platform similar to Swiggy/Zomato.

## 🚀 Quick Start

### Prerequisites

1. **Java 17+** - [Download here](https://adoptium.net/)
2. **PostgreSQL 12+** - [Download here](https://www.postgresql.org/download/)
3. **Maven 3.6+** - [Download here](https://maven.apache.org/download.cgi)
4. **Python 3.7+** (for frontend server) - [Download here](https://www.python.org/downloads/)

### Database Setup

1. **Install PostgreSQL** and start the service
2. **Create database and user**:
   ```sql
   CREATE DATABASE fooddelivery;
   CREATE USER postgres WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE fooddelivery TO postgres;
   ```

### Backend Setup

1. **Navigate to backend directory**:
   ```bash
   cd backend
   ```

2. **Update database configuration** in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/fooddelivery
       username: postgres
       password: password  # Change this to your password
   ```

3. **Run the backend**:
   ```bash
   # Windows
   mvnw.cmd spring-boot:run
   
   # Linux/Mac
   ./mvnw spring-boot:run
   ```

4. **Verify backend is running**:
   - API should be available at: `http://localhost:8080/api`
   - Check health: `http://localhost:8080/api/auth/login`

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd frontend
   ```

2. **Start the frontend server**:
   ```bash
   # Using Python
   python -m http.server 8080
   
   # Or using Python 3
   python3 -m http.server 8080
   
   # Or using Node.js (if you have it)
   npx http-server -p 8080
   ```

3. **Access the application**:
   - Open browser and go to: `http://localhost:8080`

## 🔐 Demo Accounts

All demo accounts use password: `password123`

| Role | Email | Description |
|------|-------|-------------|
| **Customer** | john.doe@gmail.com | Browse restaurants, place orders |
| **Restaurant Admin** | raj@spicepalace.com | Manage restaurant, menu, orders |
| **Delivery Partner** | ravi.delivery@gmail.com | Accept and deliver orders |
| **Platform Admin** | admin@fooddelivery.com | Manage entire platform |

## 🏗️ System Architecture

### Backend (Spring Boot)
- **Authentication**: JWT-based with role-based authorization
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: BCrypt password hashing, CORS configuration
- **API**: RESTful endpoints for all operations

### Frontend (Vanilla JavaScript)
- **UI Framework**: Tailwind CSS for responsive design
- **State Management**: Local storage for cart and user data
- **Authentication**: JWT token management
- **Real-time Updates**: Polling for order status

### Database Schema
- **Users**: Multi-role user management
- **Restaurants**: Restaurant profiles and management
- **Menu Items**: Food items with categories and pricing
- **Orders**: Complete order lifecycle management
- **Payments**: Payment processing and tracking
- **Deliveries**: Delivery assignment and tracking

## 📱 Features

### Customer Features
- ✅ Location-based restaurant discovery
- ✅ Menu browsing with categories and filters
- ✅ Shopping cart management
- ✅ Order placement and tracking
- ✅ Payment simulation
- ✅ Order history and reordering
- ✅ User profile management

### Restaurant Admin Features
- ✅ Menu management (CRUD operations)
- ✅ Order processing workflow
- ✅ Sales reports and analytics
- ✅ Restaurant profile management
- ✅ Real-time order notifications

### Delivery Partner Features
- ✅ Available delivery assignments
- ✅ Order acceptance and rejection
- ✅ Delivery status updates
- ✅ Earnings tracking
- ✅ Delivery history

### Platform Admin Features
- ✅ User management (CRUD)
- ✅ Restaurant approval and management
- ✅ Platform-wide analytics
- ✅ System monitoring and controls

## 🔧 Configuration

### Backend Configuration (`application.yml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fooddelivery
    username: postgres
    password: password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000  # 24 hours
  refresh-expiration: 604800000  # 7 days

cors:
  allowed-origins: http://localhost:3000,http://localhost:8080
```

### Frontend Configuration (`js/config.js`)

```javascript
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    // ... other configurations
};
```

## 🚀 Deployment

### Production Deployment

1. **Backend Deployment**:
   ```bash
   # Build JAR file
   mvn clean package
   
   # Run with production profile
   java -jar target/food-delivery-platform-1.0.0.jar --spring.profiles.active=prod
   ```

2. **Frontend Deployment**:
   - Deploy static files to any web server (Nginx, Apache, etc.)
   - Update API_CONFIG.BASE_URL to production backend URL

3. **Database**:
   - Use managed PostgreSQL service (AWS RDS, Google Cloud SQL, etc.)
   - Update connection strings in application.yml

### Docker Deployment

```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
COPY target/food-delivery-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```dockerfile
# Frontend Dockerfile
FROM nginx:alpine
COPY frontend/ /usr/share/nginx/html/
EXPOSE 80
```

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### API Testing
Use the provided Postman collection or test endpoints manually:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@gmail.com","password":"password123"}'

# Get restaurants
curl -X GET http://localhost:8080/api/restaurants
```

## 🔍 Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Ensure PostgreSQL is running
   - Check database credentials in application.yml
   - Verify database exists

2. **Port Already in Use**:
   - Change port in application.yml (backend)
   - Use different port for frontend server

3. **CORS Errors**:
   - Update allowed origins in application.yml
   - Ensure frontend and backend URLs match

4. **JWT Token Issues**:
   - Check token expiration settings
   - Verify JWT secret key

### Logs and Debugging

- **Backend logs**: Check console output when running Spring Boot
- **Frontend logs**: Open browser developer tools (F12)
- **Database logs**: Check PostgreSQL logs for connection issues

## 📚 API Documentation

### Authentication Endpoints
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh` - Token refresh
- `GET /auth/me` - Get current user

### Restaurant Endpoints
- `GET /restaurants` - List all restaurants
- `GET /restaurants/{id}` - Get restaurant details
- `GET /restaurants/{id}/menu` - Get restaurant menu

### Order Endpoints
- `POST /orders` - Place new order
- `GET /orders/my` - Get user orders
- `PUT /orders/{id}/cancel` - Cancel order

### Complete API documentation available at: `http://localhost:8080/swagger-ui.html` (when running)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Email: support@fooddelivery.com
- Documentation: Check README.md and code comments

---

**Happy Coding! 🍕**