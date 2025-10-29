# MySQL Setup & Testing Guide

## Quick Start Commands (Copy & Paste)

### Option 1: Docker Compose (Fastest - Recommended)

```powershell
# Navigate to project directory
cd "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"

# Start all services (MySQL + Backend + Frontend)
docker-compose up --build

# Wait 60-90 seconds for all services to start
# Then access:
# - Frontend: http://localhost
# - Backend: http://localhost:8080
# - MySQL: localhost:3306
```

### Option 2: Local Development

**PowerShell Script (Run as Administrator):**

```powershell
# Set working directory
cd "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"

# Step 1: Start MySQL with Docker
Write-Host "Starting MySQL container..." -ForegroundColor Green
docker run --name sba-mysql-dev `
  -e MYSQL_ROOT_PASSWORD=password `
  -e MYSQL_DATABASE=shribalajiattire `
  -p 3306:3306 `
  -d mysql:8.0

# Wait for MySQL to be ready
Write-Host "Waiting for MySQL to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Step 2: Set environment variables for backend
Write-Host "Setting environment variables..." -ForegroundColor Green
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="shribalajiattire"
$env:DB_USER="root"
$env:DB_PASSWORD="password"
$env:JWT_SECRET="your-256-bit-secret-key-change-this-in-production-environment"
$env:SPRING_PROFILES_ACTIVE="dev"

# Step 3: Build and test backend
Write-Host "Building backend..." -ForegroundColor Green
cd backend
mvn clean test

# Step 4: Start backend
Write-Host "Starting backend..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; mvn spring-boot:run"

# Wait for backend to start
Start-Sleep -Seconds 30

# Step 5: Start frontend
Write-Host "Starting frontend..." -ForegroundColor Green
cd ..\frontend
if (!(Test-Path "node_modules")) {
    npm install
}
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; npm run dev"

Write-Host "`n✅ Setup complete!" -ForegroundColor Green
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "Backend: http://localhost:8080" -ForegroundColor Cyan
Write-Host "MySQL: localhost:3306" -ForegroundColor Cyan
```

## Manual Step-by-Step Setup

### 1. Start MySQL

**Using Docker:**
```powershell
docker run --name sba-mysql `
  -e MYSQL_ROOT_PASSWORD=password `
  -e MYSQL_DATABASE=shribalajiattire `
  -p 3306:3306 `
  -d mysql:8.0
```

**Using Local MySQL:**
- Ensure MySQL 8.0+ is installed and running
- Create database: `CREATE DATABASE shribalajiattire;`

### 2. Configure Backend

**Set Environment Variables (PowerShell):**
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="shribalajiattire"
$env:DB_USER="root"
$env:DB_PASSWORD="password"
$env:JWT_SECRET="your-256-bit-secret-key-change-this-in-production-environment"
```

### 3. Build & Test Backend

```powershell
cd backend

# Run tests (uses H2 in-memory database)
mvn clean test

# Expected output:
# [INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

### 4. Start Backend

```powershell
mvn spring-boot:run

# Watch for these log messages:
# ✓ "HikariPool-1 - Start completed"
# ✓ "Hibernate: create table users..."
# ✓ "Created default users - Admin: admin@shribalajiattire.com"
# ✓ "Created 10 sample products"
# ✓ "Started EcommerceApplication in X seconds"
```

### 5. Verify Backend

**Open new PowerShell window:**
```powershell
# Test health endpoint
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health"

# Test products API
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/products?page=0&size=10"
$products = $response.Content | ConvertFrom-Json
Write-Host "Found $($products.content.Count) products" -ForegroundColor Green

# Expected: "Found 10 products"
```

### 6. Start Frontend

```powershell
cd ..\frontend

# Install dependencies (first time only)
npm install

# Start dev server
npm run dev

# Frontend will be available at http://localhost:3000
```

## Verification Checklist

Run these commands to verify everything is working:

### ✅ MySQL Connection Test
```powershell
# Using MySQL CLI (if installed)
mysql -h localhost -u root -ppassword -e "USE shribalajiattire; SHOW TABLES;"

# Expected tables:
# - users
# - products
# - product_images
# - product_sizes
# - product_colors
# - orders
# - order_items
```

### ✅ Backend Health Check
```powershell
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### ✅ Products API Test
```powershell
curl http://localhost:8080/api/products?page=0
# Expected: JSON with 10 products
```

### ✅ Seed Data Verification
```powershell
# Count products
$products = (Invoke-WebRequest "http://localhost:8080/api/products?page=0&size=100" | ConvertFrom-Json).content
Write-Host "Total products: $($products.Count)"
# Expected: 10

# Check first product
$products[0] | Select-Object name, price, sku
# Expected: Product details displayed
```

### ✅ Authentication Test
```powershell
# Login as admin
$loginBody = @{
    email = "admin@shribalajiattire.com"
    password = "admin123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$authData = $response.Content | ConvertFrom-Json
Write-Host "Token received: $($authData.token.Substring(0,20))..." -ForegroundColor Green
Write-Host "User: $($authData.user.name) ($($authData.user.role))" -ForegroundColor Green
```

### ✅ Frontend Test
1. Open browser: `http://localhost:3000` or `http://localhost:5173`
2. Verify products are displayed
3. Click on a product to see details
4. Add product to cart
5. Login with: `admin@shribalajiattire.com / admin123`
6. Access admin panel
7. Verify you can see products and orders

## Troubleshooting

### Problem: MySQL container won't start
```powershell
# Check if port 3306 is already in use
netstat -ano | findstr :3306

# Stop existing MySQL container
docker stop sba-mysql
docker rm sba-mysql

# Start fresh
docker run --name sba-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=shribalajiattire -p 3306:3306 -d mysql:8.0
```

### Problem: Backend can't connect to MySQL
```powershell
# Check MySQL is running
docker ps | findstr mysql

# Check MySQL logs
docker logs sba-mysql

# Test MySQL connection
docker exec -it sba-mysql mysql -uroot -ppassword -e "SELECT 1;"
```

### Problem: Backend won't start
```powershell
# Check Java version
java -version
# Expected: Java 17 or higher

# Check Maven version
mvn -version

# Clean and rebuild
cd backend
mvn clean package -DskipTests

# Check application logs
# Look for errors in the console output
```

### Problem: Tests fail
```powershell
# Run tests with verbose output
mvn test -X

# Run specific test
mvn test -Dtest=ProductServiceTest

# Skip tests temporarily
mvn spring-boot:run -DskipTests
```

### Problem: Port already in use
```powershell
# Check what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

## Database Management

### View Database Contents
```powershell
# Connect to MySQL
docker exec -it sba-mysql mysql -uroot -ppassword shribalajiattire

# Run queries
SELECT COUNT(*) FROM products;
SELECT * FROM users;
SELECT * FROM orders;
```

### Reset Database
```powershell
# Stop backend
# Ctrl+C in backend terminal

# Drop and recreate database
docker exec -it sba-mysql mysql -uroot -ppassword -e "DROP DATABASE shribalajiattire; CREATE DATABASE shribalajiattire;"

# Restart backend (will recreate tables and seed data)
cd backend
mvn spring-boot:run
```

### Backup Database
```powershell
# Export database
docker exec sba-mysql mysqldump -uroot -ppassword shribalajiattire > backup.sql

# Import database
docker exec -i sba-mysql mysql -uroot -ppassword shribalajiattire < backup.sql
```

## Clean Up

### Stop All Services
```powershell
# If using Docker Compose
docker-compose down

# If using manual setup
# 1. Stop backend (Ctrl+C in backend terminal)
# 2. Stop frontend (Ctrl+C in frontend terminal)
# 3. Stop MySQL
docker stop sba-mysql
docker rm sba-mysql
```

### Complete Clean Up
```powershell
# Remove all containers and volumes
docker-compose down -v

# Remove MySQL container
docker stop sba-mysql
docker rm sba-mysql
docker volume prune -f

# Clean Maven build
cd backend
mvn clean

# Clean frontend build
cd ..\frontend
Remove-Item -Recurse -Force node_modules, dist -ErrorAction SilentlyContinue
```

## Performance Tips

1. **Use Docker Compose** for easiest setup
2. **Allocate more memory** to Docker if needed (Settings > Resources)
3. **Use SSD** for better database performance
4. **Keep MySQL running** between development sessions
5. **Use connection pooling** (already configured in HikariCP)

## Next Steps

After successful setup:
1. ✅ Browse products at http://localhost:3000
2. ✅ Test customer flow: register, login, add to cart, checkout
3. ✅ Test admin flow: login as admin, manage products, view orders
4. ✅ Review API documentation in API.md
5. ✅ Check deployment guide in DEPLOYMENT.md

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review backend logs for error messages
3. Verify MySQL is running: `docker ps`
4. Check environment variables are set correctly
5. Ensure ports 3306, 8080, and 3000 are available

---

**Migration completed successfully! 🎉**

The application is now running on MySQL 8.0 with full functionality.
