# Create demo users for testing
$regUri = "http://localhost:8080/auth/register"
$headers = @{"Content-Type" = "application/json"}

$demoUsers = @(
    @{name="John Demo"; email="john.demo@fooddelivery.com"; phone="9876543201"; password="password123"; role="CUSTOMER"},
    @{name="Restaurant Demo"; email="restaurant.demo@fooddelivery.com"; phone="9876543202"; password="password123"; role="RESTAURANT_ADMIN"},
    @{name="Delivery Demo"; email="delivery.demo@fooddelivery.com"; phone="9876543203"; password="password123"; role="DELIVERY_PARTNER"},
    @{name="Admin Demo"; email="admin.demo@fooddelivery.com"; phone="9876543204"; password="password123"; role="PLATFORM_ADMIN"}
)

Write-Host "Creating demo users..."

foreach ($user in $demoUsers) {
    $body = $user | ConvertTo-Json
    Write-Host "Creating: $($user.email)"
    
    try {
        $response = Invoke-WebRequest -Uri $regUri -Method POST -Body $body -Headers $headers -UseBasicParsing
        Write-Host "✅ Created: $($user.email)"
    } catch {
        if ($_.Exception.Message -like "*already exists*") {
            Write-Host "⚠️  Already exists: $($user.email)"
        } else {
            Write-Host "❌ Error creating $($user.email): $($_.Exception.Message)"
        }
    }
}

Write-Host "`nTesting login with john.demo@fooddelivery.com..."
$loginUri = "http://localhost:8080/auth/login"
$loginBody = @{email = "john.demo@fooddelivery.com"; password = "password123"} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri $loginUri -Method POST -Body $loginBody -Headers $headers -UseBasicParsing
    Write-Host "✅ Login Success!"
    $data = $loginResponse.Content | ConvertFrom-Json
    Write-Host "User: $($data.user.email)"
    Write-Host "Role: $($data.user.role)"
} catch {
    Write-Host "❌ Login Failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody"
    }
}