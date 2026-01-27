# Restaurant Service

A standalone Spring Boot microservice for restaurant management in the Food Delivery Platform.

## Features

- **Restaurant Onboarding**: Register new restaurants with complete profile information
- **Restaurant Management**: Update restaurant details, operating hours, and status
- **Approval Workflow**: Platform admin approval/suspension of restaurants
- **Restaurant Discovery**: Search and filter restaurants for customers
- **Location-Based Search**: Find restaurants within specified radius
- **Operating Hours Validation**: Automatic open/close status based on time
- **Role-Based Access Control**: Different permissions for customers, restaurant admins, and platform admins

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6.x (JWT validation handled by API Gateway)
- **Database**: PostgreSQL with JPA/Hibernate
- **Mapping**: MapStruct for DTO conversions
- **Service Discovery**: Eureka Client
- **Build Tool**: Maven
- **Java Version**: 17

## Domain Model

### Restaurant Entity
- **Basic Info**: name, description, address, city, pincode
- **Location**: latitude, longitude for geo-search
- **Contact**: phone, email
- **Business**: cuisineType, imageUrl, rating, totalReviews
- **Operations**: deliveryTimeMinutes, minimumOrderAmount, deliveryFee
- **Status**: isActive, isApproved, isOpen
- **Schedule**: openingTime, closingTime
- **Audit**: createdAt, updatedAt, deletedAt (soft delete)

## API Endpoints

### Customer Endpoints (Public/Read-Only)
- `GET /restaurants` - Get all approved restaurants
- `GET /restaurants/{id}` - Get restaurant details
- `GET /restaurants/search` - Search restaurants with filters
- `GET /restaurants/city/{city}` - Get restaurants by city
- `GET /restaurants/cuisine/{cuisine}` - Get restaurants by cuisine
- `GET /restaurants/cities` - Get available cities
- `GET /restaurants/cuisines` - Get available cuisine types

### Restaurant Admin Endpoints
- `POST /restaurants` - Create new restaurant
- `PUT /restaurants/{id}` - Update restaurant details
- `PUT /restaurants/{id}/status` - Update open/close status
- `DELETE /restaurants/{id}` - Soft delete restaurant

### Platform Admin Endpoints
- `PUT /restaurants/{id}/approve` - Approve restaurant
- `PUT /restaurants/{id}/suspend` - Suspend restaurant
- `GET /restaurants/admin` - Get all restaurants (admin view)
- `GET /restaurants/admin/approval-status` - Get restaurants by approval status
- `GET /restaurants/{id}/admin` - Get restaurant details (admin view)

### Internal Service Endpoints
- `PUT /restaurants/{id}/rating` - Update restaurant rating (called by review service)

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/restaurant_service_db
    username: postgres
    password: password
```

### Service Configuration
```yaml
app:
  restaurant:
    default-delivery-radius-km: 10.0
    max-delivery-radius-km: 50.0
    rating-precision: 1
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

The service will start on port 8082.

## Sample API Usage

### Create Restaurant (Restaurant Admin)
```bash
curl -X POST http://localhost:8082/restaurants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -d '{
    "name": "Tasty Bites",
    "description": "Delicious home-style cooking",
    "address": "123 Food Street",
    "city": "Bangalore",
    "pincode": "560001",
    "latitude": 12.9716,
    "longitude": 77.5946,
    "phone": "9876543210",
    "email": "owner@tastybites.com",
    "cuisineType": "Indian",
    "openingTime": "10:00:00",
    "closingTime": "22:00:00",
    "deliveryTimeMinutes": 30,
    "minimumOrderAmount": 150.00,
    "deliveryFee": 25.00
  }'
```

### Search Restaurants (Customer)
```bash
# Search by city and cuisine
curl "http://localhost:8082/restaurants/search?city=Bangalore&cuisine=Indian"

# Search by location (within 5km radius)
curl "http://localhost:8082/restaurants/search?latitude=12.9716&longitude=77.5946&radius=5.0"

# Search by name
curl "http://localhost:8082/restaurants/search?name=pizza"
```

### Approve Restaurant (Platform Admin)
```bash
curl -X PUT http://localhost:8082/restaurants/1/approve \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Default Test Data

The service comes with pre-configured test restaurants in multiple cities:

### Bangalore
- **Spice Palace** (Indian, Approved, Open)
- **Pizza Corner** (Italian, Approved, Open)
- **Burger King Express** (Fast Food, Approved, Closed)
- **Dragon Wok** (Chinese, Approved, Open)
- **Taco Bell** (Mexican, Approved, Open)

### Mumbai
- **Mumbai Darbar** (Indian, Approved, Open)
- **Coastal Kitchen** (Seafood, Approved, Closed)

### Delhi
- **Delhi Delights** (North Indian, Approved, Open)

### Pending Approval
- **New Taste Hub** (Multi-Cuisine, Pending)
- **Fresh Bites** (Healthy, Pending)

## Business Rules

1. **Visibility**: Only active and approved restaurants are visible to customers
2. **Operating Hours**: Restaurant is open only if current time is within operating hours
3. **Soft Delete**: Restaurants are soft deleted using `deletedAt` timestamp
4. **Approval Workflow**: New restaurants require platform admin approval
5. **Status Management**: Suspended restaurants cannot be opened
6. **Location Search**: Uses Haversine formula for distance calculation

## Security & Authorization

- **Customer**: Read-only access to approved restaurants
- **Restaurant Admin**: Manage own restaurant (create, update, delete)
- **Platform Admin**: Approve/suspend any restaurant, view all restaurants
- **Internal Services**: Update restaurant ratings

## Monitoring

- **Health Endpoint**: `/restaurants/health`
- **Metrics**: Prometheus metrics available at `/actuator/prometheus`
- **Logging**: Structured logging with configurable levels

## Testing

Run tests with:
```bash
mvn test
```

The service includes integration tests that use H2 in-memory database.

## Integration with Other Services

- **Auth Service**: Role-based authorization (JWT validation via API Gateway)
- **Review Service**: Receives rating updates via internal API
- **Order Service**: Provides restaurant information for order processing
- **API Gateway**: Handles JWT validation and request routing