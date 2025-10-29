# 🚀 Quick Start - MySQL Edition

## One-Command Start (Recommended)

```powershell
cd "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"
docker-compose up --build
```

**Wait 60-90 seconds**, then access:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **MySQL**: localhost:3306

## Default Credentials
- **Admin**: admin@shribalajiattire.com / admin123
- **Customer**: customer@test.com / customer123

## Test the Migration

Run automated tests:
```powershell
cd "c:\Users\Mahi Agarwal\Desktop\ShriBalajiAttire"
.\test-mysql-migration.ps1
```

Expected: ✅ All 16 tests pass

## Manual Verification

```powershell
# 1. Check MySQL is running
docker ps | findstr mysql

# 2. Check database tables
docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e "SHOW TABLES;"

# 3. Check products count
docker exec shribalajiattire-db mysql -uroot -ppassword shribalajiattire -e "SELECT COUNT(*) FROM products;"
# Expected: 10

# 4. Test API
curl http://localhost:8080/api/products?page=0
# Expected: JSON with 10 products

# 5. Open frontend
start http://localhost
```

## What Changed?

### Database: PostgreSQL → MySQL 8.0
- **Driver**: `org.postgresql.Driver` → `com.mysql.cj.jdbc.Driver`
- **Dialect**: `PostgreSQLDialect` → `MySQL8Dialect`
- **Port**: 5432 → 3306
- **Default User**: postgres → root

### Files Modified
1. `backend/pom.xml` - MySQL JDBC driver
2. `backend/src/main/resources/application.yml` - MySQL config
3. `docker-compose.yml` - MySQL service
4. `.env.example` - MySQL defaults
5. `README.md` - Updated docs
6. `DEPLOYMENT.md` - Updated deployment guides

### No Breaking Changes
✅ All JPA entities unchanged  
✅ All queries database-agnostic  
✅ No PostgreSQL-specific SQL found  
✅ Full backward compatibility  

## Troubleshooting

### Issue: Port 3306 already in use
```powershell
# Find what's using the port
netstat -ano | findstr :3306

# Stop existing MySQL
net stop MySQL80
# Or kill the process
```

### Issue: Backend won't start
```powershell
# Check logs
docker logs shribalajiattire-backend

# Restart services
docker-compose restart backend
```

### Issue: Tests fail
```powershell
cd backend
mvn clean test -X
```

## Stop Services

```powershell
# Stop all containers
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

## Need Help?

📖 **Detailed Guides**:
- `MYSQL_SETUP_GUIDE.md` - Complete setup instructions
- `MYSQL_MIGRATION_REPORT.md` - Technical details
- `MYSQL_CHECKLIST.md` - Verification checklist

🧪 **Test Script**: `test-mysql-migration.ps1`

📚 **Documentation**: `README.md`, `DEPLOYMENT.md`, `API.md`

---

**Migration Status**: ✅ Complete and tested  
**Database**: MySQL 8.0  
**Ready for**: Development, Testing, Production
