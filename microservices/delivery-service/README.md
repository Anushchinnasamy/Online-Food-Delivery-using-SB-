# Delivery Service

A standalone Spring Boot microservice for managing delivery partners, delivery assignment, and delivery tracking in the food delivery platform.

## Overview

The Delivery Service is responsible for:
- Managing delivery partners and their availability
- Intelligent delivery assignment based on location and performance
- Tracking delivery status throughout the delivery lifecycle
- Handling delivery partner acceptance/rejection
- Automatic reassignment when deliveries are rejected
- Maintaining comprehensive delivery history

## Features

### Delivery Partner Management
- **Partner Registration**: Onboard new delivery partners
- **Profile Management**: Update partner information and vehicle details
- **Availability Control**: Manage partner active/available status
- **Location Tracking**: Real-time location updates
- **Performance Metrics**: Track ratings, success rates, and delivery counts

### Delivery Assignment
- **Intelligent Assignment**: Smart algorithm considering location, rating, and availability
- **Location-based Matching**: Find partners within delivery radius
- **Performance-based Scoring**: Prioritize high-performing partners
- **Automatic Reassignment**: Handle rejections with automatic partner reassignment

### Delivery Tracking
- **Status Management**: Complete delivery lifecycle tracking
- **Real-time Updates**: Live status updates from delivery partners
- **Timeline Tracking**: Detailed timestamps for each delivery stage
- **Customer Feedback**: Collect and store delivery ratings and feedback

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Mapping**: MapStruct
- **Service Discovery**: Netflix Eureka
- **Inter-service Communication**: OpenFeign
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Domain Model

### Delivery Partner Entity
- **Basic Info**: Name, phone, vehicle type
- **Status**: Active/inactive, available/unavailable
- **Location**: Current latitude/longitude coordinates
- **Performance**: Rating, total deliveries, success rate
- **Tracking**: Last active timestamp, creation/update dates

### Delivery Entity
- **Assignment**: Order ID, delivery partner ID
- **Status Flow**: ASSIGNED → ACCEPTED → PICKED_UP → ON_THE_WAY → DELIVERED
- **Timestamps**: Complete timeline tracking for each status
- **Metadata**: Rejection count, delivery notes, customer feedback
- **Performance**: Estimated vs actual delivery times

### Delivery Status Flow
```
ASSIGNED ──→ ACCEPTED ──→ PICKED_UP ──→ ON_THE_WAY ──→ DELIVERED
    │                                                      
    └──→ CANCELLED (can happen from ASSIGNED or ACCEPTED)
```

## API Endpoints

### Delivery Partner Management

#### Create Delivery Partner
```http
POST /delivery-partners
Content-Type: application/json

{
  "name": "John Doe",
  "phone": "+919876543210",
  "vehicleType": "MOTORCYCLE"
}
```

#### Update Delivery Partner
```http
PUT /delivery-partners/{id}
Content-Type: application/json

{
  "name": "John Doe Updated",
  "vehicleType": "CAR",
  "isActive": true,
  "isAvailable": true,
  "currentLatitude": 12.9716,
  "currentLongitude": 77.5946
}
```

#### Get Available Partners
```http
GET /delivery-partners/available
GET /delivery-partners/online
GET /delivery-partners/nearby?latitude=12.9716&longitude=77.5946&radiusKm=10
```

#### Partner Status Management
```http
PUT /delivery-partners/{id}/activate
PUT /delivery-partners/{id}/deactivate
PUT /delivery-partners/{id}/make-available
PUT /delivery-partners/{id}/make-unavailable
PUT /delivery-partners/{id}/location?latitude=12.9716&longitude=77.5946
```

### Delivery Operations

#### Assign Delivery
```http
POST /deliveries/assign
Content-Type: application/json

{
  "orderId": 123,
  "preferredDeliveryPartnerId": 456,
  "restaurantLatitude": 12.9716,
  "restaurantLongitude": 77.5946,
  "customerLatitude": 12.9352,
  "customerLongitude": 77.6245
}
```

#### Accept/Reject Delivery (Delivery Partner)
```http
PUT /deliveries/{id}/accept
X-Delivery-Partner-Id: {partnerId}

PUT /deliveries/{id}/reject
X-Delivery-Partner-Id: {partnerId}
Content-Type: application/json

{
  "rejectionReason": "Vehicle breakdown"
}
```

#### Update Delivery Status (Delivery Partner)
```http
PUT /deliveries/{id}/status
X-Delivery-Partner-Id: {partnerId}
Content-Type: application/json

{
  "deliveryStatus": "PICKED_UP",
  "deliveryNotes": "Order picked up from restaurant",
  "currentLatitude": 12.9716,
  "currentLongitude": 77.5946
}
```

#### Get Delivery Information
```http
GET /deliveries/{id}
GET /deliveries/order/{orderId}
GET /deliveries/{id}/summary
GET /deliveries/assigned (requires X-Delivery-Partner-Id header)
GET /deliveries/active (requires X-Delivery-Partner-Id header)
GET /deliveries/history?page=0&size=20 (requires X-Delivery-Partner-Id header)
```

## Database Schema

### Delivery Partners Table
- `id` (Primary Key)
- `name`
- `phone` (Unique)
- `vehicle_type` (BICYCLE, MOTORCYCLE, SCOOTER, CAR)
- `is_active`
- `is_available`
- `current_latitude`
- `current_longitude`
- `rating`
- `total_deliveries`
- `successful_deliveries`
- `last_active_at`
- `created_at`
- `updated_at`
- `deleted_at`

### Deliveries Table
- `id` (Primary Key)
- `order_id` (Unique)
- `delivery_partner_id`
- `delivery_status`
- `assigned_at`
- `accepted_at`
- `picked_up_at`
- `on_the_way_at`
- `delivered_at`
- `cancelled_at`
- `estimated_delivery_time`
- `actual_delivery_time_minutes`
- `rejection_count`
- `rejection_reason`
- `cancellation_reason`
- `delivery_notes`
- `customer_rating`
- `customer_feedback`
- `created_at`
- `updated_at`

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/delivery_service_db
    username: delivery_user
    password: delivery_password
```

### Service Discovery
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Custom Properties
```yaml
delivery:
  assignment:
    max-rejection-count: 3
    search-radius-km: 10.0
    partner-timeout-minutes: 15
  tracking:
    location-update-interval-seconds: 30
    delivery-timeout-hours: 2
```

## Running the Service

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Eureka Server (for service discovery)

### Local Development
1. **Start PostgreSQL** and create database:
   ```sql
   CREATE DATABASE delivery_service_db;
   CREATE USER delivery_user WITH PASSWORD 'delivery_password';
   GRANT ALL PRIVILEGES ON DATABASE delivery_service_db TO delivery_user;
   ```

2. **Start Eureka Server** (if not already running):
   ```bash
   cd ../service-discovery
   mvn spring-boot:run
   ```

3. **Build and run the service**:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **Verify service is running**:
   ```bash
   curl http://localhost:8086/delivery-partners/health
   curl http://localhost:8086/deliveries/health
   ```

### Using Build Script
```bash
# Make script executable
chmod +x build-and-test.bat

# Run build and test
./build-and-test.bat
```

## Delivery Assignment Algorithm

The service uses an intelligent assignment algorithm that considers:

### Partner Scoring (0-100 points)
- **Rating Score** (0-50): Based on customer ratings
- **Success Rate** (0-30): Percentage of successful deliveries
- **Experience** (0-20): Total number of deliveries completed
- **Availability Bonus** (0-10): Active and available status
- **Activity Bonus** (0-5): Recent activity within 5-15 minutes

### Location-based Matching
- Partners within configurable radius (default 10km)
- Distance calculation using Haversine formula
- Vehicle type suitability for delivery distance

### Assignment Process
1. Find online partners (active within 15 minutes)
2. Filter by location radius if coordinates available
3. Score and rank partners by performance
4. Assign to highest-scoring available partner
5. Handle rejections with automatic reassignment
6. Cancel delivery after maximum rejections (default 3)

## Security & Authorization

### Role-based Access Control
- **PLATFORM_ADMIN**: Full delivery partner management
- **SYSTEM/PLATFORM**: Delivery assignment and cancellation
- **DELIVERY_PARTNER**: Accept/reject deliveries, update status
- **CUSTOMER**: View delivery status and summary
- **RESTAURANT_ADMIN**: View delivery information for their orders

### Authentication
- JWT validation handled by API Gateway
- Role information passed via request headers
- Delivery partner ID validation for partner-specific operations

## Inter-service Communication

### Dependencies
- **Order Service**: Validate orders and update delivery status
- **Notification Service**: Send delivery updates to customers

### Service Contracts
- Feign clients with fallback mechanisms
- Circuit breaker patterns for resilience
- Graceful degradation when services unavailable

## Monitoring and Observability

### Health Checks
- `/actuator/health` - Service health status
- `/delivery-partners/health` - Partner service health
- `/deliveries/health` - Delivery service health

### Metrics
- Partner availability and utilization rates
- Delivery success and failure rates
- Average delivery times by partner
- Assignment algorithm performance
- Rejection and reassignment statistics

### Logging
- Structured logging with correlation IDs
- Delivery lifecycle audit logs
- Partner activity tracking
- Performance monitoring

## Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
The service includes:
- Unit tests for service layer business logic
- Integration tests for repositories
- Controller tests with MockMvc
- Assignment algorithm testing
- End-to-end delivery flow tests

## Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/delivery-service-1.0.0.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, staging, prod)
- `DATABASE_URL`: PostgreSQL connection URL
- `EUREKA_SERVER_URL`: Service discovery server URL
- `DELIVERY_ASSIGNMENT_RADIUS`: Default search radius in km
- `DELIVERY_MAX_REJECTIONS`: Maximum rejections before cancellation

## Future Enhancements

1. **Advanced Assignment Algorithm**
   - Machine learning-based partner selection
   - Traffic and weather condition integration
   - Dynamic pricing based on demand

2. **Real-time Tracking**
   - WebSocket-based live location updates
   - Route optimization and ETA calculation
   - Geofencing for pickup/delivery confirmation

3. **Performance Analytics**
   - Partner performance dashboards
   - Delivery time predictions
   - Customer satisfaction analytics

4. **Integration Enhancements**
   - Third-party logistics provider integration
   - Fleet management system integration
   - Advanced notification systems

## Support

For issues and questions:
- Check the logs in `logs/delivery-service.log`
- Review the API documentation
- Contact the development team

## License

This project is part of the Food Delivery Platform microservices architecture.