# Auth Service

A standalone Spring Boot microservice for authentication and authorization in the Food Delivery Platform.

## Features

- **User Registration**: Register new users with role-based access
- **User Authentication**: Login with email/password
- **JWT Tokens**: Access and refresh token management
- **Role-Based Authorization**: Support for CUSTOMER, RESTAURANT_ADMIN, DELIVERY_PARTNER, PLATFORM_ADMIN
- **Account Security**: Failed login attempt tracking and account locking
- **Token Management**: Automatic token cleanup and refresh functionality

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6.x
- **Database**: PostgreSQL with JPA/Hibernate
- **Authentication**: JWT (JSON Web Tokens)
- **Service Discovery**: Eureka Client
- **Build Tool**: Maven
- **Java Version**: 17

## API Endpoints

### Public Endpoints

- `POST /auth/register` - Register a new user
- `POST /auth/login` - Authenticate user
- `POST /auth/refresh` - Refresh access token
- `GET /auth/health` - Health check

### Protected Endpoints

- `GET /auth/validate` - Validate token and get user info
- `GET /auth/me` - Get current user details
- `POST /auth/change-password` - Change user password
- `POST /auth/logout` - Logout user
- `POST /auth/logout-all` - Logout from all devices

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/auth_service_db
    username: postgres
    password: password
```

### JWT Configuration
```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days
```

## Running the Service

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server (for service discovery)

### Steps
1. Start PostgreSQL database
2. Start Eureka Server
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8081.

## Sample API Usage

### Register User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "password": "password123",
    "role": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Validate Token
```bash
curl -X GET http://localhost:8081/auth/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Default Test Users

The service comes with pre-configured test users (password: `password123`):

- **Admin**: admin@fooddelivery.com (PLATFORM_ADMIN)
- **Restaurant Owner**: raj@spicepalace.com (RESTAURANT_ADMIN)
- **Delivery Partner**: ravi.delivery@gmail.com (DELIVERY_PARTNER)
- **Customer**: john.doe@gmail.com (CUSTOMER)

## Security Features

- **Password Encryption**: BCrypt with strength 12
- **Account Locking**: After 5 failed login attempts (30 minutes lock)
- **Token Expiration**: Configurable access and refresh token expiration
- **CORS Support**: Configurable cross-origin resource sharing
- **Input Validation**: Comprehensive request validation

## Monitoring

- **Health Endpoint**: `/actuator/health`
- **Metrics**: Prometheus metrics available at `/actuator/prometheus`
- **Logging**: Structured logging with configurable levels

## Testing

Run tests with:
```bash
mvn test
```

The service includes integration tests that use H2 in-memory database.