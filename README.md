# Shri Balaji Attire - E-Commerce Platform

A full-stack e-commerce web application for selling premium branded shirts, built with React, Spring Boot, and MySQL.

## 🚀 Features

### Customer Features
- **Product Catalog**: Browse products with search, filters (size, color, price), and sorting
- **Product Details**: View detailed product information with multiple images
- **Shopping Cart**: Add/remove items, adjust quantities, persistent cart in localStorage
- **User Authentication**: JWT-based secure registration and login
- **Checkout**: Complete order flow with shipping address collection
- **Payment Integration**: Stripe payment (test mode) and mock payment for demo
- **Order History**: View past orders and order details
- **Responsive Design**: Mobile-friendly UI with Tailwind CSS

### Admin Features
- **Product Management**: Create, update, and delete products with image upload
- **Order Management**: View all orders and update order status
- **Analytics Dashboard**: Revenue and order statistics (last 30 days)

## 🛠️ Tech Stack

### Frontend
- **React 18** with Vite
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API calls
- **React Toastify** for notifications
- **Lucide React** for icons
- **Stripe React** for payment integration

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Actuator
- **MySQL 8.0** (production) / **H2** (demo)
- **JWT** for authentication
- **Stripe Java SDK** for payments
- **Lombok** for boilerplate reduction

### DevOps
- **Docker** & **Docker Compose**
- **GitHub Actions** for CI/CD
- **Maven** for backend build
- **npm** for frontend build

## 📋 Prerequisites

- **Java 17+** (for local development)
- **Node.js 18+** and npm
- **MySQL 8.0+** (or use Docker)
- **Maven 3.8+** (or use Docker)
- **Docker & Docker Compose** (recommended)

## 🚀 Quick Start with Docker

The fastest way to run the entire application:

```bash
# Clone the repository
git clone <repository-url>
cd ShriBalajiAttire

# Copy environment file and configure
cp .env.example .env
# Edit .env with your Stripe keys (optional for demo)

# Build and run all services
docker-compose up --build

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080
# Database: localhost:3306
```

The application will automatically seed sample data including:
- **Admin user**: admin@shribalajiattire.com / admin123
- **Customer user**: customer@test.com / customer123
- **10 sample products**

## 💻 Local Development Setup

### Backend Setup

```bash
cd backend

# Configure database (MySQL)
# Update src/main/resources/application.yml with your DB credentials

# Or use H2 in-memory database
export SPRING_PROFILES_ACTIVE=h2

# Build and run
mvn clean install
mvn spring-boot:run

# Backend will start on http://localhost:8080
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env

# Start development server
npm run dev

# Frontend will start on http://localhost:3000
```

## 🔐 Environment Variables

### Backend (.env or environment)

```env
# Database (MySQL)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shribalajiattire
DB_USER=root
DB_PASSWORD=password

# JWT Secret (change in production!)
JWT_SECRET=your-256-bit-secret-key-change-this-in-production-environment

# Stripe (get from https://dashboard.stripe.com/test/apikeys)
STRIPE_SECRET_KEY=sk_test_your_key
STRIPE_WEBHOOK_SECRET=whsec_your_secret

# File Upload
UPLOAD_DIR=./uploads
UPLOAD_BASE_URL=http://localhost:8080/api/uploads

# CORS
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Frontend (.env)

```env
VITE_API_URL=http://localhost:8080/api
```

## 📚 API Documentation

### Authentication Endpoints

```
POST /api/auth/register
POST /api/auth/login
```

### Public Product Endpoints

```
GET  /api/products?page=0&size=12&q=search&sizeFilter=M&colorFilter=Blue&minPrice=1000&maxPrice=5000&sort=price-asc
GET  /api/products/{id}
GET  /api/products/slug/{slug}
```

### Customer Endpoints (Requires Authentication)

```
POST /api/checkout
GET  /api/orders?page=0&size=10
GET  /api/orders/{id}
```

### Admin Endpoints (Requires ADMIN role)

```
POST   /api/admin/products (multipart/form-data)
PUT    /api/admin/products/{id} (multipart/form-data)
DELETE /api/admin/products/{id}
GET    /api/admin/orders?page=0&size=10
PUT    /api/admin/orders/{id}/status
GET    /api/admin/analytics
```

### Example Request: Create Product

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F 'product={"name":"Blue Shirt","sku":"SBA-001","description":"A nice blue shirt","price":1999,"sizes":["M","L","XL"],"colors":["Blue"],"stock":50};type=application/json' \
  -F 'images=@shirt1.jpg' \
  -F 'images=@shirt2.jpg'
```

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn test
```

### Frontend Tests

```bash
cd frontend
npm run lint
npm run build
```

## 📦 Build for Production

### Backend

```bash
cd backend
mvn clean package -DskipTests
# JAR file will be in target/ecommerce-backend-1.0.0.jar
```

### Frontend

```bash
cd frontend
npm run build
# Build output will be in dist/
```

## 🚢 Deployment

### Deploy with Docker

```bash
# Build images
docker-compose build

# Run in production mode
docker-compose -f docker-compose.yml up -d
```

### Deploy to Cloud Platforms

#### Heroku

**Backend:**
```bash
cd backend
heroku create shribalajiattire-api
heroku addons:create heroku-postgresql:mini
heroku config:set JWT_SECRET=your-secret
heroku config:set STRIPE_SECRET_KEY=your-key
git subtree push --prefix backend heroku main
```

**Frontend:**
```bash
cd frontend
# Build and deploy to Netlify/Vercel
npm run build
# Upload dist/ folder
```

#### AWS/Azure/GCP

1. **Backend**: Deploy Spring Boot JAR to EC2/App Service/Cloud Run
2. **Database**: Use managed PostgreSQL (RDS/Azure Database/Cloud SQL)
3. **Frontend**: Deploy to S3+CloudFront/Azure Storage/Cloud Storage
4. **Images**: Use S3/Azure Blob/Cloud Storage for product images

## 🔒 Security Considerations

- **JWT Secret**: Change the default JWT secret in production
- **Database**: Use strong passwords and restrict access
- **CORS**: Configure allowed origins properly
- **Stripe**: Use production keys only in production
- **HTTPS**: Always use HTTPS in production
- **Environment Variables**: Never commit secrets to Git

## 📁 Project Structure

```
ShriBalajiAttire/
├── backend/
│   ├── src/main/java/com/shribalajiattire/
│   │   ├── config/          # Security, CORS, Data initialization
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # JWT, UserDetails
│   │   ├── service/         # Business logic
│   │   └── exception/       # Exception handlers
│   ├── src/main/resources/
│   │   └── application.yml  # Configuration
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable components
│   │   ├── context/         # Auth & Cart context
│   │   ├── pages/           # Page components
│   │   │   └── admin/       # Admin pages
│   │   ├── utils/           # API client
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
├── .env.example
└── README.md
```

## 🎨 Default Credentials

**Admin Account:**
- Email: admin@shribalajiattire.com
- Password: admin123

**Customer Account:**
- Email: customer@test.com
- Password: customer123

## 🐛 Troubleshooting

### Backend won't start
- Check if PostgreSQL is running
- Verify database credentials in application.yml
- Check if port 8080 is available
- Try using H2 profile: `SPRING_PROFILES_ACTIVE=h2`

### Frontend can't connect to backend
- Verify VITE_API_URL in .env
- Check CORS configuration in backend
- Ensure backend is running on port 8080

### Docker issues
- Run `docker-compose down -v` to clean volumes
- Check Docker logs: `docker-compose logs backend`
- Ensure ports 80, 8080, 5432 are not in use

### Database connection errors
- Wait for PostgreSQL to be ready (healthcheck)
- Check database credentials
- Verify network connectivity

## 📝 Sample Data

The application seeds 10 sample products on first run:
- Classic White Formal Shirt (₹1,499)
- Blue Oxford Business Shirt (₹1,699)
- Black Slim Fit Shirt (₹1,899)
- Pink Casual Shirt (₹2,099)
- Navy Blue Formal Shirt (₹2,299)
- Grey Checkered Shirt (₹2,499)
- Light Blue Linen Shirt (₹2,699)
- Burgundy Party Shirt (₹2,899)
- Mint Green Casual Shirt (₹3,099)
- Charcoal Formal Shirt (₹3,299)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Spring Boot for the robust backend framework
- React and Vite for the modern frontend
- Tailwind CSS for beautiful styling
- Stripe for payment processing
- PostgreSQL for reliable data storage

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Email: support@shribalajiattire.com

---

**Built with ❤️ for modern e-commerce**
