# Deployment Guide

This guide covers various deployment options for Shri Balaji Attire e-commerce platform.

## Table of Contents
- [Docker Deployment](#docker-deployment)
- [Heroku Deployment](#heroku-deployment)
- [AWS Deployment](#aws-deployment)
- [Azure Deployment](#azure-deployment)
- [Production Checklist](#production-checklist)

## Docker Deployment

### Prerequisites
- Docker and Docker Compose installed
- Domain name (optional)
- SSL certificate (recommended)

### Steps

1. **Clone and Configure**
```bash
git clone <repository-url>
cd ShriBalajiAttire
cp .env.example .env
```

2. **Update Environment Variables**
Edit `.env` file with production values:
```env
JWT_SECRET=<generate-strong-secret-256-bits>
STRIPE_SECRET_KEY=sk_live_your_production_key
DB_PASSWORD=<strong-database-password>
CORS_ORIGINS=https://yourdomain.com
```

3. **Build and Run**
```bash
docker-compose up -d --build
```

4. **Verify Deployment**
```bash
docker-compose ps
docker-compose logs -f
```

5. **Setup Nginx Reverse Proxy (Optional)**
```nginx
server {
    listen 80;
    server_name yourdomain.com;
    
    location / {
        proxy_pass http://localhost:80;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## Heroku Deployment

### Backend Deployment

1. **Create Heroku App**
```bash
cd backend
heroku create shribalajiattire-api
```

2. **Add MySQL Database**
```bash
heroku addons:create jawsdb:kitefin
# Or use ClearDB MySQL
heroku addons:create cleardb:ignite
```

3. **Set Environment Variables**
```bash
heroku config:set JWT_SECRET=your-secret-key
heroku config:set STRIPE_SECRET_KEY=sk_live_xxx
heroku config:set STRIPE_WEBHOOK_SECRET=whsec_xxx
heroku config:set CORS_ORIGINS=https://your-frontend.netlify.app
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

4. **Create Procfile**
```bash
echo "web: java -Dserver.port=$PORT -jar target/*.jar" > Procfile
```

5. **Deploy**
```bash
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

### Frontend Deployment (Netlify)

1. **Build Configuration**
Create `netlify.toml`:
```toml
[build]
  command = "npm run build"
  publish = "dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[build.environment]
  VITE_API_URL = "https://shribalajiattire-api.herokuapp.com/api"
```

2. **Deploy**
```bash
cd frontend
npm install -g netlify-cli
netlify deploy --prod
```

## AWS Deployment

### Architecture
- **Frontend**: S3 + CloudFront
- **Backend**: Elastic Beanstalk or ECS
- **Database**: RDS MySQL
- **Images**: S3 bucket

### Backend on Elastic Beanstalk

1. **Create Application**
```bash
cd backend
eb init -p java-17 shribalajiattire-api
eb create production
```

2. **Configure Environment**
```bash
eb setenv JWT_SECRET=xxx STRIPE_SECRET_KEY=xxx DB_HOST=xxx DB_PASSWORD=xxx
```

3. **Deploy**
```bash
mvn clean package
eb deploy
```

### Frontend on S3 + CloudFront

1. **Build Frontend**
```bash
cd frontend
npm run build
```

2. **Create S3 Bucket**
```bash
aws s3 mb s3://shribalajiattire-frontend
aws s3 sync dist/ s3://shribalajiattire-frontend --acl public-read
```

3. **Configure S3 for Static Hosting**
```bash
aws s3 website s3://shribalajiattire-frontend --index-document index.html --error-document index.html
```

4. **Create CloudFront Distribution**
- Origin: S3 bucket
- Default Root Object: index.html
- Error Pages: 404 → /index.html (200)

### RDS MySQL Setup

1. **Create Database**
```bash
aws rds create-db-instance \
  --db-instance-identifier shribalajiattire-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0 \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 20
```

2. **Update Backend Configuration**
```bash
eb setenv DB_HOST=<rds-endpoint> DB_PORT=3306 DB_NAME=shribalajiattire DB_USER=admin DB_PASSWORD=<password>
```

## Azure Deployment

### Backend on Azure App Service

1. **Create Resource Group**
```bash
az group create --name ShriBalaji --location eastus
```

2. **Create App Service Plan**
```bash
az appservice plan create --name ShriBalaji-Plan --resource-group ShriBalaji --sku B1 --is-linux
```

3. **Create Web App**
```bash
az webapp create --resource-group ShriBalaji --plan ShriBalaji-Plan --name shribalajiattire-api --runtime "JAVA:17-java17"
```

4. **Configure Environment**
```bash
az webapp config appsettings set --resource-group ShriBalaji --name shribalajiattire-api --settings \
  JWT_SECRET=xxx \
  STRIPE_SECRET_KEY=xxx \
  DB_HOST=xxx
```

5. **Deploy**
```bash
cd backend
mvn clean package
az webapp deploy --resource-group ShriBalaji --name shribalajiattire-api --src-path target/*.jar
```

### Frontend on Azure Static Web Apps

1. **Create Static Web App**
```bash
az staticwebapp create --name shribalajiattire --resource-group ShriBalaji --source https://github.com/yourrepo --location eastus --branch main --app-location "/frontend" --output-location "dist"
```

2. **Configure Build**
GitHub Actions workflow will be auto-created.

### Azure Database for MySQL

1. **Create Database**
```bash
az mysql flexible-server create --resource-group ShriBalaji --name shribalajiattire-db --location eastus --admin-user admin --admin-password <password> --sku-name Standard_B1ms --version 8.0.21
```

2. **Configure Firewall**
```bash
az mysql flexible-server firewall-rule create --resource-group ShriBalaji --name shribalajiattire-db --rule-name AllowAzure --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
```

## Production Checklist

### Security
- [ ] Change default JWT secret to strong random value
- [ ] Use production Stripe keys
- [ ] Enable HTTPS/SSL
- [ ] Configure CORS properly
- [ ] Use strong database passwords
- [ ] Enable database encryption
- [ ] Set up firewall rules
- [ ] Implement rate limiting
- [ ] Enable security headers

### Performance
- [ ] Enable database connection pooling
- [ ] Configure caching (Redis)
- [ ] Enable CDN for static assets
- [ ] Optimize images
- [ ] Enable gzip compression
- [ ] Set up database indexes
- [ ] Configure auto-scaling

### Monitoring
- [ ] Set up application logging
- [ ] Configure error tracking (Sentry)
- [ ] Enable health checks
- [ ] Set up uptime monitoring
- [ ] Configure alerts
- [ ] Enable database monitoring
- [ ] Track performance metrics

### Backup & Recovery
- [ ] Configure automated database backups
- [ ] Set up backup retention policy
- [ ] Test restore procedures
- [ ] Document recovery process
- [ ] Set up file storage backups

### Environment Variables (Production)

```env
# Backend
SPRING_PROFILES_ACTIVE=prod
DB_HOST=production-db-host
DB_NAME=shribalajiattire
DB_USER=postgres
DB_PASSWORD=<strong-password>
JWT_SECRET=<256-bit-random-secret>
STRIPE_SECRET_KEY=sk_live_xxx
STRIPE_WEBHOOK_SECRET=whsec_xxx
UPLOAD_DIR=/app/uploads
UPLOAD_BASE_URL=https://api.yourdomain.com/api/uploads
CORS_ORIGINS=https://yourdomain.com

# Frontend
VITE_API_URL=https://api.yourdomain.com/api
```

### SSL/TLS Configuration

**Using Let's Encrypt with Certbot:**
```bash
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

**Nginx SSL Configuration:**
```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;
    
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    
    # Rest of configuration...
}
```

### Database Migration

For production database setup:

```sql
-- Create database
CREATE DATABASE shribalajiattire;

-- Create user
CREATE USER shribalajiattire_user WITH PASSWORD 'strong_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE shribalajiattire TO shribalajiattire_user;
```

### Monitoring Commands

```bash
# Check application health
curl https://api.yourdomain.com/actuator/health

# View logs
docker-compose logs -f backend
heroku logs --tail -a shribalajiattire-api

# Database status
docker-compose exec postgres pg_isready

# Check disk space
df -h
```

## Troubleshooting

### Common Issues

**Issue: Application won't start**
- Check environment variables
- Verify database connectivity
- Review application logs
- Check port availability

**Issue: Database connection failed**
- Verify database credentials
- Check firewall rules
- Ensure database is running
- Verify network connectivity

**Issue: CORS errors**
- Update CORS_ORIGINS environment variable
- Check frontend API URL configuration
- Verify backend CORS configuration

**Issue: File upload fails**
- Check UPLOAD_DIR permissions
- Verify disk space
- Check file size limits
- Review nginx/proxy configuration

## Support

For deployment assistance:
- Check application logs
- Review Docker/Heroku/Cloud provider logs
- Consult platform-specific documentation
- Open an issue on GitHub

---

**Remember**: Always test in a staging environment before deploying to production!
