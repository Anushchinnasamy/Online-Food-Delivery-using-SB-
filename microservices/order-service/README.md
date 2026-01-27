# Order Service

A standalone Spring Boot microservice for order management in the Food Delivery Platform.

## Features

- **Order Creation**: Create orders with menu item snapshots
- **Order Lifecycle Management**: Complete order status flow from PLACED to DELIVERED
- **Order State Validation**: Enforce valid order status transitions
- **Order History**: Comprehensive order history for users, restaurants, and delivery partners
- **Menu Item Snapshots**: Store menu item data at time of order (immutable)
- **Inter-service Communication**: Validate restaurants and menu items via Feign clients
- **Role-based Access Control**: Different permissions for customers, restaurant admins, and delivery partners

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6.x (JWT validation handled by API Gateway)
- **Database**: PostgreSQL with JPA/Hibernate
- **Mapping**: MapStruct for DTO conversions
- **Service Discovery**: Eureka Client
- **Inter-service Communication**: OpenFeign with Hystrix fallback
- **Build Tool**: Maven
- **Java Version**: 17

## Domain Model

### Order Entity
- **Basic Info**: orderNumber (unique), userId, restaurantId, totalAmount
- **Status**: orderStatus, paymentStatus
- **Delivery**: deliveryPartnerId, estimatedDeliveryTime, actualDeliveryTime
- **Additional**: specialInstructions, cancellationReason, cancelledBy
- **Audit**: createdAt, updatedAt

### OrderItem Entity
- **Order Link**: orderId (foreign key to Order)
- **Menu Snapshot**: menuItemId, itemName, itemPrice (snapshots)
- **Order Details**: quantity, subtotal, specialInstructions

## Order Status Flow

### Allowed States & Transitions
- **PLACED** → ACCEPTED, CANCELLED
- **ACCEPTED** → PREPARING, CANCELLED
- **PREPARING** → READY_FOR_PICKUP
- **READY_FOR_PICKUP** → PICKED_UP
- **PICKED_UP** → DELIVERED
- **DELIVERED** → (final state)
- **CANCELLED** → (final state)

### Business Rules
- Orders cannot be modified after PREPARING status
- Orders can only be cancelled before PREPARING status
- Invalid transitions throw exceptions
- Menu item data is stored as snapshots (immutable)

## API Endpoints

### Customer Endpoints
- `POST /orders` - Create new order
- `GET /orders/{id}` - Get order details
- `GET /orders/number/{orderNumber}` - Get order by order number
- `GET /orders/user/{userId}` - Get user's order history
- `GET /orders/user/{userId}/page` - Get user's orders with pagination
- `PUT /orders/{id}/cancel` - Cancel order

### Restaurant Admin Endpoints
- `GET /orders/restaurant/{restaurantId}` - Get restaurant's orders
- `GET /orders/restaurant/{restaurantId}/page` - Get restaurant's orders with pagination
- `GET /orders/restaurant/{restaurantId}/active` - Get active orders for restaurant
- `PUT /orders/{id}/status` - Update order status

### Delivery Partner Endpoints
- `GET /orders/assigned/{deliveryPartnerId}` - Get assigned orders
- `GET /orders/ready-for-pickup` - Get orders ready for pickup

### Internal/Admin Endpoints
- `PUT /orders/{id}/assign-delivery-partner` - Assign delivery partner to order

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/order_service_db
    username: postgres
    password: password
```

### Service Configuration
```yaml
app:
  order:
    default-page-size: 20
    max-page-size: 100
    estimated-delivery-time-minutes: 35
    max-items-per-order: 50
```

## Running the Service

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server (for service discovery)
- Restaurant Service (for restaurant validation)
- Menu Service (for menu item validation)

### Steps
1. Start PostgreSQL database
2. Start Eureka Server
3. Start Restaurant Service
4. Start Menu Service
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8084.

## Sample API Usage

### Create Order (Customer)
```bash
curl -X POST http://localhost:8084/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-User-Id: 1" \
  -d '{
    "restaurantId": 1,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2,
        "specialInstructions": "Extra spicy"
      },
      {
        "menuItemId": 3,
        "quantity": 1
      }
    ],
    "specialInstructions": "Please ring the bell"
  }'
```

### Get Order Details (Customer)
```bash
curl -X GET http://localhost:8084/orders/1 \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-User-Id: 1" \
  -H "X-User-Role: CUSTOMER"
```

### Update Order Status (Restaurant Admin)
```bash
curl -X PUT http://localhost:8084/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-Restaurant-Id: 1" \
  -d '{
    "orderStatus": "ACCEPTED",
    "notes": "Order confirmed, preparing now"
  }'
```

### Get Restaurant Orders (Restaurant Admin)
```bash
curl -X GET http://localhost:8084/orders/restaurant/1/active \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-Restaurant-Id: 1"
```

### Cancel Order (Customer)
```bash
curl -X PUT http://localhost:8084/orders/1/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-User-Id: 1" \
  -H "X-User-Role: CUSTOMER" \
  -d '{
    "reason": "Changed my mind"
  }'
```

## Default Test Data

The service comes with pre-configured test orders:

### Sample Orders
1. **ORD-20241127-143022-1001** - User 1, Spice Palace, DELIVERED (₹650)
   - Butter Chicken (₹320) x1
   - Chicken Biryani (₹380) x1

2. **ORD-20241127-143022-1002** - User 2, Pizza Corner, PREPARING (₹420)
   - Margherita Pizza (₹250) x1
   - Garlic Bread (₹120) x1
   - Caesar Salad (₹180) x1

3. **ORD-20241127-143022-1003** - User 3, Burger King, READY_FOR_PICKUP (₹380)
   - Whopper Burger (₹220) x1
   - French Fries (₹80) x2

4. **ORD-20241127-143022-1004** - User 1, Dragon Wok, PICKED_UP (₹500)
   - Kung Pao Chicken (₹280) x1
   - Vegetable Fried Rice (₹220) x1

5. **ORD-20241127-143022-1005** - User 2, Spice Palace, CANCELLED (₹280)
   - Paneer Tikka Masala (₹280) x1

## Business Rules

1. **Order Immutability**: Orders cannot be modified after PREPARING status
2. **Menu Snapshots**: Menu item data is stored as snapshots at order time
3. **Status Validation**: Only valid status transitions are allowed
4. **Cancellation Rules**: Orders can only be cancelled before PREPARING
5. **Access Control**: Users can only access their own orders
6. **Restaurant Ownership**: Restaurant admins can only manage their restaurant's orders
7. **Delivery Assignment**: Orders must be READY_FOR_PICKUP to assign delivery partner

## Security & Authorization

- **Customer**: Create and view own orders, cancel orders before PREPARING
- **Restaurant Admin**: View and manage orders for own restaurant, update order status
- **Delivery Partner**: View assigned orders and orders ready for pickup
- **Internal Services**: Access to order validation and delivery partner assignment

## Inter-Service Communication

- **Menu Service**: Validates menu items and fetches item details for snapshots
- **Restaurant Service**: Validates restaurant existence and status
- **Fallback Mechanism**: Service continues to work with degraded functionality if dependencies are unavailable

## Order Number Generation

- **Format**: ORD-YYYYMMDD-HHMMSS-XXXX
- **Example**: ORD-20241127-143022-1001
- **Uniqueness**: Guaranteed unique across all orders
- **Retry Logic**: Up to 10 attempts to generate unique number

## Monitoring

- **Health Endpoint**: `/orders/health`
- **Metrics**: Prometheus metrics available at `/actuator/prometheus`
- **Logging**: Structured logging with configurable levels
- **Feign Logging**: Debug level logging for inter-service calls

## Testing

Run tests with:
```bash
mvn test
```

The service includes integration tests that use H2 in-memory database.

## Error Handling

- **OrderNotFoundException**: When order is not found
- **InvalidOrderStateException**: When invalid status transition is attempted
- **UnauthorizedAccessException**: When trying to access orders without permission
- **MenuItemNotAvailableException**: When menu items are unavailable
- **Validation Errors**: Comprehensive field validation with detailed error messages
- **Global Exception Handler**: Consistent error response format across all endpoints

## Performance Considerations

- **Database Indexing**: Optimized indexes for common query patterns
- **Pagination**: Support for paginated results to handle large datasets
- **Lazy Loading**: OrderItems are loaded lazily to improve performance
- **Connection Pooling**: HikariCP for efficient database connections
- **Caching**: Consider adding Redis for frequently accessed order data