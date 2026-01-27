# Test authentication flow
$regUri = "http://localhost:8081/auth/register"
$loginUri = "http://localhost:8081/auth/login"
$headers = @{"Content-Type" = "application/json"}

# Generate unique user data
$email = "quicktest$(Get-Random)@example.com"
$phone = "9$(Get-Random -Minimum 100000000 -Maximum 999999999)"

Write-Host "Testing authentication flow..."
Write-Host "Email: $email"
Write-Host "Phone: $phone"

# Step 1: Register
$regBody = @{
    name = "Quick Test"
    email = $email
    phone = $phone
    password = "password123"
    role = "CUSTOMER"
} | ConvertTo-Json

Write-Host "`n1. Registering user..."
try {
    $regResponse = Invoke-WebRequest -Uri $regUri -Method POST -Body $regBody -Headers $headers -UseBasicParsing
    Write-Host "✅ Registration Success! Status: $($regResponse.StatusCode)"
} catch {
    Write-Host "❌ Registration Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
    exit 1
}

# Step 2: Login
$loginBody = @{
    email = $email
    password = "password123"
} | ConvertTo-Json

Write-Host "`n2. Logging in with same user..."
try {
    $loginResponse = Invoke-WebRequest -Uri $loginUri -Method POST -Body $loginBody -Headers $headers -UseBasicParsing
    Write-Host "✅ Login Success! Status: $($loginResponse.StatusCode)"
    
    $loginData = $loginResponse.Content | ConvertFrom-Json
    Write-Host "User: $($loginData.user.email)"
    Write-Host "Role: $($loginData.user.role)"
    Write-Host "Access Token: $($loginData.access_token -ne $null)"
    Write-Host "Refresh Token: $($loginData.refresh_token -ne $null)"
} catch {
    Write-Host "❌ Login Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}

Write-Host "`nTest completed!"