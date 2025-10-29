# API Documentation

Complete REST API documentation for Shri Balaji Attire e-commerce platform.

## Base URL

```
Development: http://localhost:8080/api
Production: https://api.yourdomain.com/api
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Response Format

### Success Response
```json
{
  "id": 1,
  "name": "Product Name",
  "price": 1999.00
}
```

### Error Response
```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "code": 400,
  "timestamp": "2025-10-29T15:18:11"
}
```

## Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "CUSTOMER"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** Same as register

---

### Products (Public)

#### Get All Products
```http
GET /api/products?page=0&size=12&q=shirt&sizeFilter=M&sizeFilter=L&colorFilter=Blue&minPrice=1000&maxPrice=5000&sort=price-asc
```

**Query Parameters:**
- `page` (optional): Page number, default 0
- `size` (optional): Items per page, default 12
- `q` (optional): Search query
- `sizeFilter` (optional, multiple): Filter by sizes (S, M, L, XL, XXL)
- `colorFilter` (optional, multiple): Filter by colors
- `minPrice` (optional): Minimum price in INR
- `maxPrice` (optional): Maximum price in INR
- `sort` (optional): Sort order
  - `price-asc`: Price low to high
  - `price-desc`: Price high to low
  - `name-asc`: Name A to Z
  - `name-desc`: Name Z to A

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Classic White Formal Shirt",
      "slug": "classic-white-formal-shirt",
      "sku": "SBA-0001",
      "description": "Premium cotton formal shirt...",
      "priceCents": 149900,
      "price": 1499.00,
      "currency": "INR",
      "sizes": ["S", "M", "L", "XL", "XXL"],
      "colors": ["White"],
      "images": ["http://localhost:8080/api/uploads/image1.jpg"],
      "stock": 50,
      "active": true,
      "createdAt": "2025-10-29T10:00:00",
      "updatedAt": "2025-10-29T10:00:00"
    }
  ],
  "totalPages": 5,
  "totalElements": 50,
  "size": 12,
  "number": 0
}
```

#### Get Product by ID
```http
GET /api/products/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "Classic White Formal Shirt",
  "slug": "classic-white-formal-shirt",
  "sku": "SBA-0001",
  "description": "Premium cotton formal shirt...",
  "priceCents": 149900,
  "price": 1499.00,
  "currency": "INR",
  "sizes": ["S", "M", "L", "XL", "XXL"],
  "colors": ["White"],
  "images": ["http://localhost:8080/api/uploads/image1.jpg"],
  "stock": 50,
  "active": true,
  "createdAt": "2025-10-29T10:00:00",
  "updatedAt": "2025-10-29T10:00:00"
}
```

#### Get Product by Slug
```http
GET /api/products/slug/{slug}
```

**Response:** Same as Get Product by ID

---

### Orders (Authenticated)

#### Create Order (Checkout)
```http
POST /api/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "cartItems": [
    {
      "productId": 1,
      "quantity": 2,
      "size": "M",
      "color": "White"
    }
  ],
  "shipping": {
    "fullName": "John Doe",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India",
    "phone": "+91 9876543210"
  },
  "paymentMethod": "stripe"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "userEmail": "john@example.com",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Classic White Formal Shirt",
      "quantity": 2,
      "unitPriceCents": 149900,
      "unitPrice": 1499.00,
      "size": "M",
      "color": "White"
    }
  ],
  "totalCents": 299800,
  "total": 2998.00,
  "currency": "INR",
  "shippingAddress": {
    "fullName": "John Doe",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India",
    "phone": "+91 9876543210"
  },
  "status": "PAID",
  "paymentId": "pi_xxx",
  "paymentMethod": "stripe",
  "createdAt": "2025-10-29T15:00:00",
  "updatedAt": "2025-10-29T15:00:00"
}
```

#### Get User Orders
```http
GET /api/orders?page=0&size=10
Authorization: Bearer <token>
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "userEmail": "john@example.com",
      "items": [...],
      "totalCents": 299800,
      "total": 2998.00,
      "currency": "INR",
      "shippingAddress": {...},
      "status": "SHIPPED",
      "paymentId": "pi_xxx",
      "paymentMethod": "stripe",
      "createdAt": "2025-10-29T15:00:00",
      "updatedAt": "2025-10-29T15:30:00"
    }
  ],
  "totalPages": 2,
  "totalElements": 15,
  "size": 10,
  "number": 0
}
```

#### Get Order by ID
```http
GET /api/orders/{id}
Authorization: Bearer <token>
```

**Response:** Same as order object in Create Order

---

### Admin - Products (Admin Only)

#### Create Product
```http
POST /api/admin/products
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

product: {
  "name": "Blue Oxford Shirt",
  "sku": "SBA-0011",
  "description": "Premium Oxford weave shirt",
  "price": 1999.00,
  "sizes": ["M", "L", "XL"],
  "colors": ["Blue"],
  "stock": 30
}
images: [file1.jpg, file2.jpg]
```

**Response:**
```json
{
  "id": 11,
  "name": "Blue Oxford Shirt",
  "slug": "blue-oxford-shirt",
  "sku": "SBA-0011",
  "description": "Premium Oxford weave shirt",
  "priceCents": 199900,
  "price": 1999.00,
  "currency": "INR",
  "sizes": ["M", "L", "XL"],
  "colors": ["Blue"],
  "images": [
    "http://localhost:8080/api/uploads/uuid1.jpg",
    "http://localhost:8080/api/uploads/uuid2.jpg"
  ],
  "stock": 30,
  "active": true,
  "createdAt": "2025-10-29T15:00:00",
  "updatedAt": "2025-10-29T15:00:00"
}
```

#### Update Product
```http
PUT /api/admin/products/{id}
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

product: {
  "name": "Blue Oxford Shirt - Updated",
  "sku": "SBA-0011",
  "description": "Updated description",
  "price": 2199.00,
  "sizes": ["S", "M", "L", "XL"],
  "colors": ["Blue", "Navy"],
  "stock": 40
}
images: [file3.jpg]
```

**Response:** Same as Create Product

#### Delete Product
```http
DELETE /api/admin/products/{id}
Authorization: Bearer <admin-token>
```

**Response:** 204 No Content

---

### Admin - Orders (Admin Only)

#### Get All Orders
```http
GET /api/admin/orders?page=0&size=10
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "userEmail": "john@example.com",
      "items": [...],
      "totalCents": 299800,
      "total": 2998.00,
      "currency": "INR",
      "shippingAddress": {...},
      "status": "SHIPPED",
      "paymentId": "pi_xxx",
      "paymentMethod": "stripe",
      "createdAt": "2025-10-29T15:00:00",
      "updatedAt": "2025-10-29T15:30:00"
    }
  ],
  "totalPages": 10,
  "totalElements": 95,
  "size": 10,
  "number": 0
}
```

#### Update Order Status
```http
PUT /api/admin/orders/{id}/status
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "status": "SHIPPED"
}
```

**Valid statuses:**
- `PENDING`
- `PAID`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Response:**
```json
{
  "id": 1,
  "status": "SHIPPED",
  ...
}
```

#### Get Analytics
```http
GET /api/admin/analytics
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "revenueCents": 15000000,
  "orderCount": 75
}
```

---

### File Uploads

#### Get Uploaded File
```http
GET /api/uploads/{filename}
```

**Response:** Image file (JPEG/PNG)

---

## Error Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing or invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Duplicate resource |
| 500 | Internal Server Error |

## Rate Limiting

Currently no rate limiting is implemented. For production, consider implementing:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated users

## Pagination

All list endpoints support pagination with these parameters:
- `page`: Page number (0-indexed)
- `size`: Items per page

Response includes:
- `content`: Array of items
- `totalPages`: Total number of pages
- `totalElements`: Total number of items
- `size`: Items per page
- `number`: Current page number

## Examples

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'
```

**Get Products:**
```bash
curl http://localhost:8080/api/products?page=0&size=12&q=shirt&sort=price-asc
```

**Create Order:**
```bash
curl -X POST http://localhost:8080/api/checkout \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d @checkout.json
```

### Using JavaScript (Axios)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Login
const login = async () => {
  const response = await api.post('/auth/login', {
    email: 'john@example.com',
    password: 'password123'
  });
  const token = response.data.token;
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

// Get Products
const getProducts = async () => {
  const response = await api.get('/products', {
    params: {
      page: 0,
      size: 12,
      q: 'shirt',
      sort: 'price-asc'
    }
  });
  return response.data;
};

// Create Order
const checkout = async (orderData) => {
  const response = await api.post('/checkout', orderData);
  return response.data;
};
```

## Postman Collection

Import this collection into Postman for easy testing:

```json
{
  "info": {
    "name": "Shri Balaji Attire API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"John Doe\",\n  \"email\": \"john@example.com\",\n  \"password\": \"password123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    }
  ]
}
```

---

For more information, see the [README.md](README.md) and [DEPLOYMENT.md](DEPLOYMENT.md).
