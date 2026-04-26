# Simple API Testing Script
$baseUrl = "http://localhost:8081/api"
$passed = 0
$failed = 0

Write-Host "API ENDPOINT TESTING REPORT" -ForegroundColor Cyan
Write-Host "=============================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Login Customer
Write-Host "1. Testing Customer Login..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"john.doe@gmail.com","password":"password123"}'
    $customerToken = $response.accessToken
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 2: Login Restaurant Admin
Write-Host "2. Testing Restaurant Admin Login..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"raj@spicepalace.com","password":"password123"}'
    $restaurantToken = $response.accessToken
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 3: Get All Restaurants
Write-Host "3. Testing Get All Restaurants..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/restaurants" -Method GET
    $restaurantId = $response[0].id
    Write-Host " PASS ($($response.Count) restaurants)" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 4: Get Restaurant by ID
Write-Host "4. Testing Get Restaurant by ID..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/restaurants/$restaurantId" -Method GET
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 5: Search Restaurants
Write-Host "5. Testing Search Restaurants by City..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/restaurants/search?city=Bangalore" -Method GET
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 6: Get Menu Items
Write-Host "6. Testing Get Menu Items for Restaurant..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/menu-items/restaurant/$restaurantId" -Method GET
    $menuItemId = $response[0].id
    Write-Host " PASS ($($response.Count) items)" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 7: Get Menu Item by ID
Write-Host "7. Testing Get Menu Item by ID..." -NoNewline
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/menu-items/$menuItemId" -Method GET
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 8: Place Order
Write-Host "8. Testing Place Order..." -NoNewline
try {
    $orderBody = @{
        restaurantId = $restaurantId
        items = @(
            @{
                menuItemId = $menuItemId
                quantity = 2
                specialInstructions = "Extra spicy"
            }
        )
        deliveryAddress = "123 Main Street, HSR Layout, Bangalore"
        deliveryLatitude = 12.9082
        deliveryLongitude = 77.6476
        customerPhone = "9876543216"
        specialInstructions = "Ring the doorbell twice"
        paymentMethod = "CREDIT_CARD"
    } | ConvertTo-Json -Depth 10
    
    $headers = @{ "Authorization" = "Bearer $customerToken" }
    $response = Invoke-RestMethod -Uri "$baseUrl/orders" -Method POST -ContentType "application/json" -Body $orderBody -Headers $headers
    $orderId = $response.id
    Write-Host " PASS (Order #$($response.orderNumber))" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL - $($_.Exception.Message)" -ForegroundColor Red
    $failed++
}

# Test 9: Get My Orders
Write-Host "9. Testing Get My Orders..." -NoNewline
try {
    $headers = @{ "Authorization" = "Bearer $customerToken" }
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/my" -Method GET -Headers $headers
    Write-Host " PASS ($($response.Count) orders)" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 10: Get Order by ID
Write-Host "10. Testing Get Order by ID..." -NoNewline
try {
    $headers = @{ "Authorization" = "Bearer $customerToken" }
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/$orderId" -Method GET -Headers $headers
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 11: Get Restaurant Orders
Write-Host "11. Testing Get Restaurant Orders..." -NoNewline
try {
    $headers = @{ "Authorization" = "Bearer $restaurantToken" }
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/restaurant/$restaurantId" -Method GET -Headers $headers
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 12: Update Order Status
Write-Host "12. Testing Update Order Status..." -NoNewline
try {
    $headers = @{ "Authorization" = "Bearer $restaurantToken" }
    $statusBody = '{"status":"ACCEPTED"}'
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/$orderId/status" -Method PUT -ContentType "application/json" -Body $statusBody -Headers $headers
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Test 13: Get My Restaurant
Write-Host "13. Testing Get My Restaurant..." -NoNewline
try {
    $headers = @{ "Authorization" = "Bearer $restaurantToken" }
    $response = Invoke-RestMethod -Uri "$baseUrl/restaurants/my" -Method GET -Headers $headers
    Write-Host " PASS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host " FAIL" -ForegroundColor Red
    $failed++
}

# Summary
Write-Host ""
Write-Host "=============================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "=============================" -ForegroundColor Cyan
Write-Host "Total Tests: $($passed + $failed)"
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
$successRate = [math]::Round(($passed/($passed + $failed))*100, 2)
Write-Host "Success Rate: $successRate%"
Write-Host ""
