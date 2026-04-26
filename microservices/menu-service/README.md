# Menu Service

A standalone Spring Boot microservice for menu and menu item management in the Food Delivery Platform.

## Features

- **Menu Item CRUD**: Create, read, update, and delete menu items
- **Category Management**: Organize menu items by categories
- **Price Management**: Update menu item prices
- **Availability Toggle**: Control menu item availability in real-time
- **Soft Delete**: Soft delete menu items using deletedAt timestamp
- **Restaurant-based Menu Listing**: Get menu items by restaurant
- **Search & Filter**: Search by name, filter by category, vegetarian/non-vegetarian
- **Bulk Operations**: Bulk update availability by restaurant or category
- **Inter-service Communication**: Validate restaurant existence via Feign client

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

### MenuItem Entity
- **Basic Info**: name, description, price, category
- **Restaurant Link**: restaurantId (no foreign key constraint)
- **Media**: imageUrl
- **Status**: isAvailable, isVegetarian
- **Audit**: createdAt, updatedAt, deletedAt (soft delete)

## API Endpoints

### Restaurant Admin Endpoints
- `POST /menus` - Create new menu item
- `PUT /menus/{id}` - Update menu item details
- `PUT /menus/{id}/availability` - Toggle menu item availability
- `DELETE /menus/{id}` - Soft delete menu item
- `GET /menus/{id}/admin` - Get menu item details (admin view)
- `GET /menus/restaurant/{restaurantId}/admin` - Get all menu items for restaurant
- `PUT /menus/restaurant/{restaurantId}/availability` - Bulk update restaurant menu availability
- `PUT /menus/restaurant/{restaurantId}/category/{category}/availability` - Bulk update category availability

### Customer Endpoints
- `GET /menus/restaurant/{restaurantId}` - Get available menu items by restaurant
- `GET /menus/restaurant/{restaurantId}/page` - Get menu items with pagination
- `GET /menus/restaurant/{restaurantId}/category/{category}` - Get menu items by category
- `GET /menus/restaurant/{restaurantId}/vegetarian` - Get vegetarian menu items
- `GET /menus/restaurant/{restaurantId}/non-vegetarian` - Get non-vegetarian menu items
- `GET /menus/restaurant/{restaurantId}/search?name=` - Search menu items by name
- `GET /menus/restaurant/{restaurantId}/categories` - Get available categories

### Internal Service Endpoints
- `POST /menus/batch` - Get menu items by IDs (for order validation)

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/menu_service_db
    username: postgres
    password: password
```

### Service Configuration
```yaml
app:
  menu:
    default-page-size: 20
    max-page-size: 100
    max-items-per-restaurant: 1000
```

## Running the Service

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server (for service discovery)
- Restaurant Service (for restaurant validation)

### Steps
1. Start PostgreSQL database
2. Start Eureka Server
3. Start Restaurant Service
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8083.

## Sample API Usage

### Create Menu Item (Restaurant Admin)
```bash
curl -X POST http://localhost:8083/menus \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-Restaurant-Id: 1" \
  -d '{
    "restaurantId": 1,
    "name": "Chicken Tikka",
    "description": "Grilled chicken marinated in spices",
    "price": 250.00,
    "category": "Main Course",
    "imageUrl": "https://example.com/chicken-tikka.jpg",
    "isVegetarian": false
  }'
```

### Get Menu Items (Customer)
```bash
# Get all available menu items for a restaurant
curl "http://localhost:8083/menus/restaurant/1"

# Get menu items by category
curl "http://localhost:8083/menus/restaurant/1/category/Main%20Course"

# Search menu items by name
curl "http://localhost:8083/menus/restaurant/1/search?name=chicken"

# Get vegetarian items only
curl "http://localhost:8083/menus/restaurant/1/vegetarian"
```

### Update Menu Item Availability (Restaurant Admin)
```bash
curl -X PUT http://localhost:8083/menus/1/availability \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-Restaurant-Id: 1" \
  -d '{"is_available": false}'
```

### Bulk Update Category Availability (Restaurant Admin)
```bash
curl -X PUT http://localhost:8083/menus/restaurant/1/category/Desserts/availability \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "X-Restaurant-Id: 1" \
  -d '{"is_available": false}'
```

## Default Test Data

The service comes with pre-configured menu items for test restaurants:

### Spice Palace (Restaurant ID: 1) - Indian Cuisine
- Butter Chicken (₹320) - Non-Veg, Main Course
- Paneer Tikka Masala (₹280) - Veg, Main Course
- Garlic Naan (₹80) - Veg, Breads
- Chicken Biryani (₹380) - Non-Veg, Rice & Biryani
- Vegetable Biryani (₹320) - Veg, Rice & Biryani
- Mango Lassi (₹120) - Veg, Beverages

### Pizza Corner (Restaurant ID: 2) - Italian Cuisine
- Margherita Pizza (₹250) - Veg, Main Course
- Pepperoni Pizza (₹320) - Non-Veg, Main Course
- Chicken Alfredo Pasta (₹280) - Non-Veg, Main Course
- Caesar Salad (₹180) - Veg, Salads
- Garlic Bread (₹120) - Veg, Appetizers
- Tiramisu (₹150) - Veg, Desserts

### Burger King Express (Restaurant ID: 3) - Fast Food
- Whopper Burger (₹220) - Non-Veg, Fast Food
- Veggie Burger (₹180) - Veg, Fast Food
- Chicken Nuggets (₹160) - Non-Veg, Fast Food
- French Fries (₹80) - Veg, Snacks
- Coca Cola (₹60) - Veg, Beverages

### Dragon Wok (Restaurant ID: 4) - Chinese Cuisine
- Kung Pao Chicken (₹280) - Non-Veg, Chinese
- Vegetable Fried Rice (₹220) - Veg, Chinese
- Sweet and Sour Pork (₹320) - Non-Veg, Chinese
- Spring Rolls (₹150) - Veg, Appetizers
- Hot and Sour Soup (₹120) - Veg, Soups

### Taco Bell (Restaurant ID: 5) - Mexican Cuisine
- Chicken Quesadilla (₹200) - Non-Veg, Fast Food
- Veggie Burrito (₹180) - Veg, Fast Food
- Beef Tacos (₹160) - Non-Veg, Fast Food
- Nachos Supreme (₹140) - Veg, Snacks

## Business Rules

1. **Visibility**: Only available menu items are visible to customers
2. **Soft Delete**: Menu items are soft deleted using `deletedAt` timestamp
3. **Restaurant Ownership**: Restaurant admins can only manage their own menu items
4. **Unique Names**: Menu item names must be unique within a restaurant
5. **Price Validation**: Prices must be positive values
6. **Category Organization**: Menu items are organized by categories for better browsing

## Security & Authorization

- **Customer**: Read-only access to available menu items
- **Restaurant Admin**: Full CRUD operations on own restaurant's menu items
- **Internal Services**: Access to batch operations for order validation
- **Restaurant Validation**: Optional validation via Restaurant Service (with fallback)

## Inter-Service Communication

- **Restaurant Service**: Validates restaurant existence and status via Feign client
- **Fallback Mechanism**: Service continues to work even if Restaurant Service is unavailable
- **Order Service**: Provides menu item details for order processing via batch endpoint

## Monitoring

- **Health Endpoint**: `/menus/health`
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

- **MenuItemNotFoundException**: When menu item is not found
- **MenuItemAlreadyExistsException**: When trying to create duplicate menu item
- **UnauthorizedAccessException**: When trying to access other restaurant's menu items
- **Validation Errors**: Comprehensive field validation with detailed error messages
- **Global Exception Handler**: Consistent error response format across all endpoints