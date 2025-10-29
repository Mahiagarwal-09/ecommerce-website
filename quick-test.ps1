# Quick MySQL Migration Test Script
# Simple version with basic checks

Write-Host "`n=== Shri Balaji Attire - MySQL Quick Test ===`n" -ForegroundColor Cyan

$projectDir = "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"
Set-Location $projectDir

$passed = 0
$failed = 0

# Test 1: Check Docker
Write-Host "1. Checking Docker..." -NoNewline
try {
    docker info | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host " ✓" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 2: Check pom.xml
Write-Host "2. Checking MySQL in pom.xml..." -NoNewline
$pomContent = Get-Content "$projectDir\backend\pom.xml" -Raw
if ($pomContent -match "mysql-connector-j" -and $pomContent -notmatch "postgresql") {
    Write-Host " ✓" -ForegroundColor Green
    $passed++
} else {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 3: Check application.yml
Write-Host "3. Checking MySQL in application.yml..." -NoNewline
$appYml = Get-Content "$projectDir\backend\src\main\resources\application.yml" -Raw
if ($appYml -match "jdbc:mysql" -and $appYml -match "MySQL8Dialect") {
    Write-Host " ✓" -ForegroundColor Green
    $passed++
} else {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 4: Check docker-compose.yml
Write-Host "4. Checking MySQL in docker-compose.yml..." -NoNewline
$dockerCompose = Get-Content "$projectDir\docker-compose.yml" -Raw
if ($dockerCompose -match "mysql:8.0" -and $dockerCompose -notmatch "postgres") {
    Write-Host " ✓" -ForegroundColor Green
    $passed++
} else {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 5: Start services
Write-Host "5. Starting services (this takes 60-90 seconds)..." -ForegroundColor Yellow
docker-compose up -d --build | Out-Null
Start-Sleep -Seconds 60

# Test 6: Check MySQL container
Write-Host "6. Checking MySQL container..." -NoNewline
$mysqlContainer = docker ps --filter "name=shribalajiattire-db" --format "{{.Status}}"
if ($mysqlContainer -match "Up") {
    Write-Host " ✓" -ForegroundColor Green
    $passed++
} else {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 7: Check backend container
Write-Host "7. Checking backend container..." -NoNewline
$backendContainer = docker ps --filter "name=shribalajiattire-backend" --format "{{.Status}}"
if ($backendContainer -match "Up") {
    Write-Host " ✓" -ForegroundColor Green
    $passed++
} else {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 8: Check database
Write-Host "8. Checking database exists..." -NoNewline
try {
    $dbCheck = docker exec shribalajiattire-db mysql -uroot -ppassword -e "SHOW DATABASES;" 2>&1
    if ($dbCheck -match "shribalajiattire") {
        Write-Host " ✓" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 9: Check tables
Write-Host "9. Checking database tables..." -NoNewline
try {
    $showTablesCmd = "SHOW TABLES;"
    $tables = docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e $showTablesCmd 2>&1
    if ($tables -match "users" -and $tables -match "products") {
        Write-Host " ✓" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 10: Check API
Write-Host "10. Checking API endpoint..." -NoNewline
try {
    $health = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5 -UseBasicParsing
    if ($health.StatusCode -eq 200) {
        Write-Host " ✓" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 11: Check products
Write-Host "11. Checking products API..." -NoNewline
try {
    $productsUri = "http://localhost:8080/api/products?page=0&size=10"
    $products = Invoke-RestMethod -Uri $productsUri -TimeoutSec 10
    if ($products.content -and $products.content.Count -gt 0) {
        Write-Host " ✓ ($($products.content.Count) products)" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Test 12: Check frontend
Write-Host "12. Checking frontend..." -NoNewline
try {
    $frontend = Invoke-WebRequest -Uri "http://localhost" -TimeoutSec 5 -UseBasicParsing
    if ($frontend.StatusCode -eq 200) {
        Write-Host " ✓" -ForegroundColor Green
        $passed++
    } else {
        Write-Host " ✗" -ForegroundColor Red
        $failed++
    }
} catch {
    Write-Host " ✗" -ForegroundColor Red
    $failed++
}

# Summary
Write-Host "`n=== Test Results ===" -ForegroundColor Cyan
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })

if ($failed -eq 0) {
    Write-Host "`n✅ All tests passed! MySQL migration successful!" -ForegroundColor Green
    Write-Host "`nAccess the application:" -ForegroundColor Cyan
    Write-Host "  Frontend: http://localhost" -ForegroundColor White
    Write-Host "  Backend:  http://localhost:8080" -ForegroundColor White
    Write-Host "  MySQL:    localhost:3306" -ForegroundColor White
    Write-Host "`nDefault credentials:" -ForegroundColor Cyan
    Write-Host "  Admin:    admin@shribalajiattire.com / admin123" -ForegroundColor White
    Write-Host "  Customer: customer@test.com / customer123" -ForegroundColor White
} else {
    Write-Host "`n❌ Some tests failed." -ForegroundColor Red
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "  Check logs: docker-compose logs" -ForegroundColor White
    Write-Host "  Restart: docker-compose down; docker-compose up --build" -ForegroundColor White
}

Write-Host "`nTo stop: docker-compose down" -ForegroundColor Gray
Write-Host ""
