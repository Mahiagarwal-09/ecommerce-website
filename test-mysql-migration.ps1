

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Shri Balaji Attire - MySQL Test" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$ErrorActionPreference = "Continue"
$testsPassed = 0
$testsFailed = 0

function Test-Step {
    param(
        [string]$Name,
        [scriptblock]$Test
    )
    
    Write-Host "Testing: $Name..." -ForegroundColor Yellow -NoNewline
    try {
        & $Test
        Write-Host " ✓ PASS" -ForegroundColor Green
        $script:testsPassed++
        return $true
    } catch {
        Write-Host " ✗ FAIL" -ForegroundColor Red
        Write-Host "  Error: $_" -ForegroundColor Red
        $script:testsFailed++
        return $false
    }
}

# Change to project directory
$projectDir = "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"
Set-Location $projectDir

Write-Host "Project Directory: $projectDir`n" -ForegroundColor Gray

# Test 1: Check Docker is running
Test-Step "Docker is running" {
    $dockerInfo = docker info 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Docker is not running"
    }
}

# Test 2: Check if MySQL container exists
$mysqlRunning = $false
Test-Step "MySQL container status" {
    $containers = docker ps --format "{{.Names}}" 2>&1
    if ($containers -match "sba-mysql|shribalajiattire-db") {
        $script:mysqlRunning = $true
    } else {
        Write-Host "`n  Note: MySQL not running. Will start with docker-compose." -ForegroundColor Yellow
    }
}

# Test 3: Check pom.xml has MySQL dependency
Test-Step "MySQL dependency in pom.xml" {
    $pomContent = Get-Content "$projectDir\backend\pom.xml" -Raw
    if ($pomContent -notmatch "mysql-connector-j") {
        throw "MySQL connector not found in pom.xml"
    }
    if ($pomContent -match "postgresql") {
        throw "PostgreSQL dependency still present in pom.xml"
    }
}

# Test 4: Check application.yml has MySQL config
Test-Step "MySQL configuration in application.yml" {
    $appYml = Get-Content "$projectDir\backend\src\main\resources\application.yml" -Raw
    if ($appYml -notmatch "jdbc:mysql") {
        throw "MySQL JDBC URL not found in application.yml"
    }
    if ($appYml -match "PostgreSQL") {
        throw "PostgreSQL references still present in application.yml"
    }
    if ($appYml -notmatch "MySQL8Dialect") {
        throw "MySQL8Dialect not configured"
    }
}

# Test 5: Check docker-compose.yml has MySQL service
Test-Step "MySQL service in docker-compose.yml" {
    $dockerCompose = Get-Content "$projectDir\docker-compose.yml" -Raw
    if ($dockerCompose -notmatch "mysql:8.0") {
        throw "MySQL 8.0 image not found in docker-compose.yml"
    }
    if ($dockerCompose -match "postgres") {
        throw "PostgreSQL references still present in docker-compose.yml"
    }
}

# Test 6: Start services with docker-compose
Write-Host "`nStarting services with docker-compose..." -ForegroundColor Cyan
Write-Host "(This may take 60-90 seconds)..." -ForegroundColor Gray

$composeUp = Test-Step "Docker Compose up" {
    $output = docker-compose up -d 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "docker-compose up failed: $output"
    }
    Start-Sleep -Seconds 45
}

if ($composeUp) {
    # Test 7: Check MySQL container is running
    Test-Step "MySQL container running" {
        $mysqlContainer = docker ps --filter "name=shribalajiattire-db" --format "{{.Status}}"
        if (-not $mysqlContainer -or $mysqlContainer -notmatch "Up") {
            throw "MySQL container is not running"
        }
    }

    # Test 8: Check MySQL is accepting connections
    Test-Step "MySQL accepting connections" {
        $mysqlTest = docker exec shribalajiattire-db mysqladmin ping -h localhost -ppassword 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "MySQL is not accepting connections"
        }
    }

    # Test 9: Check database exists
    Test-Step "Database shribalajiattire exists" {
        $dbCheck = docker exec shribalajiattire-db mysql -uroot -ppassword -e "SHOW DATABASES LIKE 'shribalajiattire';" 2>&1
        if ($dbCheck -notmatch "shribalajiattire") {
            throw "Database shribalajiattire not found"
        }
    }

    # Wait for backend to fully start
    Write-Host "`nWaiting for backend to start..." -ForegroundColor Gray
    Start-Sleep -Seconds 30

    # Test 10: Check backend container is running
    Test-Step "Backend container running" {
        $backendContainer = docker ps --filter "name=shribalajiattire-backend" --format "{{.Status}}"
        if (-not $backendContainer -or $backendContainer -notmatch "Up") {
            throw "Backend container is not running"
        }
    }

    # Test 11: Check backend health endpoint
    Test-Step "Backend health endpoint" {
        $maxRetries = 5
        $retryCount = 0
        $success = $false
        
        while ($retryCount -lt $maxRetries -and -not $success) {
            try {
                $health = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5 -UseBasicParsing
                if ($health.StatusCode -eq 200) {
                    $success = $true
                }
            } catch {
                $retryCount++
                if ($retryCount -lt $maxRetries) {
                    Start-Sleep -Seconds 10
                }
            }
        }
        
        if (-not $success) {
            throw "Backend health endpoint not responding"
        }
    }

    # Test 12: Check products API returns data
    Test-Step "Products API returns data" {
        $uri = "http://localhost:8080/api/products?page=0&size=10"
        $products = Invoke-RestMethod -Uri $uri -TimeoutSec 10
        if (-not $products.content -or $products.content.Count -eq 0) {
            throw "No products returned from API"
        }
        Write-Host "`n  Found $($products.content.Count) products" -ForegroundColor Gray
    }

    # Test 13: Check database tables exist
    Test-Step "Database tables created" {
        $tables = docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e "SHOW TABLES;" 2>&1
        $requiredTables = @("users", "products", "orders", "order_items", "product_images")
        foreach ($table in $requiredTables) {
            if ($tables -notmatch $table) {
                throw "Required table $table not found"
            }
        }
    }

    # Test 14: Check seed data exists
    Test-Step "Seed data loaded" {
        $userCountCmd = 'SELECT COUNT(*) FROM users;'
        $productCountCmd = 'SELECT COUNT(*) FROM products;'
        $userCount = docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e $userCountCmd 2>&1
        $productCount = docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e $productCountCmd 2>&1
        
        if ($userCount -notmatch "2|3|4|5|6|7|8|9") {
            throw "Expected at least 2 users, found: $userCount"
        }
        if ($productCount -notmatch "10") {
            throw "Expected 10 products, found: $productCount"
        }
    }

    # Test 15: Check frontend container is running
    Test-Step "Frontend container running" {
        $frontendContainer = docker ps --filter "name=shribalajiattire-frontend" --format "{{.Status}}"
        if (-not $frontendContainer -or $frontendContainer -notmatch "Up") {
            throw "Frontend container is not running"
        }
    }

    # Test 16: Check frontend is accessible
    Test-Step "Frontend accessible" {
        try {
            $frontend = Invoke-WebRequest -Uri "http://localhost" -TimeoutSec 5 -UseBasicParsing
            if ($frontend.StatusCode -ne 200) {
                throw "Frontend returned status code: $($frontend.StatusCode)"
            }
        } catch {
            throw "Frontend not accessible: $_"
        }
    }
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Test Results" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Tests Passed: $testsPassed" -ForegroundColor Green
Write-Host "Tests Failed: $testsFailed" -ForegroundColor $(if ($testsFailed -eq 0) { "Green" } else { "Red" })

if ($testsFailed -eq 0) {
    Write-Host "`n✅ All tests passed! MySQL migration successful!" -ForegroundColor Green
    Write-Host "`nAccess the application:" -ForegroundColor Cyan
    Write-Host "  Frontend: http://localhost" -ForegroundColor White
    Write-Host "  Backend:  http://localhost:8080" -ForegroundColor White
    Write-Host "  MySQL:    localhost:3306" -ForegroundColor White
    Write-Host "`nDefault credentials:" -ForegroundColor Cyan
    Write-Host "  Admin:    admin@shribalajiattire.com / admin123" -ForegroundColor White
    Write-Host "  Customer: customer@test.com / customer123" -ForegroundColor White
} else {
    Write-Host "`n❌ Some tests failed. Check the errors above." -ForegroundColor Red
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "  1. Check Docker logs: docker-compose logs" -ForegroundColor White
    Write-Host "  2. Check backend logs: docker logs shribalajiattire-backend" -ForegroundColor White
    Write-Host "  3. Check MySQL logs: docker logs shribalajiattire-db" -ForegroundColor White
    Write-Host "  4. Restart services: docker-compose down; docker-compose up --build" -ForegroundColor White
}

Write-Host "`nTo stop services: docker-compose down" -ForegroundColor Gray
Write-Host "To view logs: docker-compose logs -f" -ForegroundColor Gray
Write-Host ""
