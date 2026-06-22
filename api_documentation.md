# VillageConnect REST API Documentation

This document describes the REST API endpoints available in the **VillageConnect** platform. All APIs return standard JSON responses and use HTTP status codes for errors.

For authenticated endpoints, include the JWT token in the request header as:
`Authorization: Bearer <your_jwt_token>`

---

## 1. Authentication Endpoints

### Register User
* **Endpoint:** `POST /api/auth/register`
* **Access:** Public
* **Request Body:**
```json
{
  "name": "Ramesh Kumar",
  "email": "ramesh@gmail.com",
  "phone": "9988776655",
  "password": "customer123",
  "role": "CUSTOMER"
}
```
* **Response (200 OK):**
```json
{
  "message": "User registered successfully",
  "userId": 4,
  "email": "ramesh@gmail.com",
  "role": "CUSTOMER"
}
```

### Login User
* **Endpoint:** `POST /api/auth/login`
* **Access:** Public
* **Request Body:**
```json
{
  "email": "customer@villageconnect.com",
  "password": "customer123"
}
```
* **Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 4,
  "name": "Ramesh Kumar",
  "email": "customer@villageconnect.com",
  "role": "CUSTOMER"
}
```

---

## 2. Shop Endpoints

### List / Search Shops
* **Endpoint:** `GET /api/shops`
* **Access:** Public
* **Query Parameters (Optional):**
  * `query`: Search term (supports village name, category, or business description)
  * `category`: Filter by category (e.g. `Groceries`, `Agriculture`, `Electrical`, `Medical`)
  * `village`: Filter by village name
* **Response (200 OK):**
```json
[
  {
    "shopId": 1,
    "shopName": "Srinivasa Kirana Store",
    "ownerName": "Srinivasa Rao",
    "phone": "9848012345",
    "village": "Ramapuram",
    "address": "Main Road, Ramapuram Village",
    "category": "Groceries",
    "description": "General daily groceries and fresh milk.",
    "latitude": 16.5061,
    "longitude": 80.6482,
    "ownerId": 2
  }
]
```

### Get Shop Details
* **Endpoint:** `GET /api/shops/{id}`
* **Access:** Public
* **Response (200 OK):**
```json
{
  "shopId": 1,
  "shopName": "Srinivasa Kirana Store",
  "ownerName": "Srinivasa Rao",
  "phone": "9848012345",
  "village": "Ramapuram",
  "address": "Main Road, Ramapuram Village",
  "category": "Groceries",
  "description": "General daily groceries.",
  "latitude": 16.5061,
  "longitude": 80.6482,
  "ownerId": 2
}
```

### Create Shop
* **Endpoint:** `POST /api/shops`
* **Access:** Authenticated (`SHOP_OWNER`, `ADMIN`)
* **Request Body:** Similar to Shop DTO (without `shopId`).
* **Response (200 OK):** Mapped Shop object.

---

## 3. Product Endpoints

### List and Search Products
* **Endpoint:** `GET /api/products`
* **Access:** Public
* **Query Parameters:**
  * `query`: Keyword search (English/Telugu). Translates Telugu and performs semantic expansion if direct SQL search has no matches.
  * `category`: Filter by category
* **Response (200 OK):**
```json
[
  {
    "productId": 1,
    "shopId": 1,
    "shopName": "Srinivasa Kirana Store",
    "productName": "White Sugar",
    "price": 42.00,
    "quantity": 50,
    "category": "Groceries",
    "description": "Premium white sugar, price per kg.",
    "imageUrl": "...",
    "stockStatus": "IN_STOCK"
  }
]
```

### List Shop Products
* **Endpoint:** `GET /api/products/shop/{shopId}`
* **Access:** Public

---

## 4. Product Demand Request Endpoints

### Submit Product Request
* **Endpoint:** `POST /api/requests`
* **Access:** Authenticated (Any role)
* **Request Body:**
```json
{
  "shopId": 2,
  "productName": "Hybrid Tomato Seeds",
  "quantityRequested": 5
}
```
* **Response (200 OK):**
```json
{
  "requestId": 1,
  "userId": 4,
  "userName": "Ramesh Kumar",
  "userPhone": "9988776655",
  "shopId": 2,
  "shopName": "Rythu Seva Center",
  "productName": "Hybrid Tomato Seeds",
  "quantityRequested": 5,
  "status": "PENDING",
  "requestDate": "2026-06-21T10:45:00"
}
```

### View Owner Requests
* **Endpoint:** `GET /api/requests/shop/{shopId}`
* **Access:** Authenticated (`SHOP_OWNER` of target shop, `ADMIN`)

### Update Request Status
* **Endpoint:** `PUT /api/requests/{requestId}/status`
* **Access:** Authenticated (`SHOP_OWNER` of target shop, `ADMIN`)
* **Query Parameters:**
  * `status`: `FULFILLED` or `CANCELLED`

---

## 5. Review Endpoints

### Submit Review
* **Endpoint:** `POST /api/reviews`
* **Access:** Authenticated (Any role)
* **Request Body:**
```json
{
  "shopId": 1,
  "rating": 5,
  "comment": "Fast and friendly!"
}
```

---

## 6. AI Endpoints

### AI Chatbot dialog
* **Endpoint:** `POST /api/ai/chat`
* **Access:** Authenticated (Any role)
* **Request Body:**
```json
{
  "message": "Where can I buy seeds?"
}
```
* **Response (200 OK):**
```json
{
  "reply": "You can buy Hybrid Tomato Seeds and Neem fertilizer at the **Rythu Seva Center** in Ramapuram village! Note: Tomato seeds are currently out of stock, but you can submit a Product Request on their details page."
}
```

### AI Personalized Recommendations
* **Endpoint:** `GET /api/ai/recommendations`
* **Access:** Authenticated (Any role)
* **Response (200 OK):**
```json
{
  "recommendations": "<ul><li><strong>White Sugar</strong> (Groceries) at Srinivasa Kirana Store. Reason: Great price for daily staples.</li></ul>"
}
```

---

## 7. Admin Endpoints

### System Stats
* **Endpoint:** `GET /api/admin/stats`
* **Access:** Authenticated (`ADMIN`)
* **Response (200 OK):**
```json
{
  "totalUsers": 4,
  "totalShops": 4,
  "totalProducts": 9,
  "totalRequests": 1
}
```
