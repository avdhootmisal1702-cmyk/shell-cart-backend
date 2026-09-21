# Shell-Cart Backend

A secure RESTful e-commerce backend built with **Spring Boot**, **Spring Security**, **JWT**, **Spring Data JPA**, and **MySQL**.

Shell-Cart provides user authentication, role-based authorization, product management, shopping cart functionality, and transactional order processing with stock management.

---

## 🚀 Features

* User registration and authentication
* JWT-based authentication
* Role-based access control (`USER` / `ADMIN`)
* Secure password hashing using BCrypt
* Product CRUD operations
* Shopping cart management
* Order checkout and order history
* Automatic inventory/stock reduction
* Transactional checkout using `@Transactional`
* Pessimistic database locking for stock consistency
* User ownership validation for carts and orders
* Request validation using Jakarta Validation
* Centralized exception handling
* Swagger/OpenAPI API documentation
* Environment-variable based configuration for secrets
* MySQL database integration

---

## 🛠️ Tech Stack

| Technology        | Purpose                        |
| ----------------- | ------------------------------ |
| Java              | Backend programming language   |
| Spring Boot       | Application framework          |
| Spring MVC        | REST API development           |
| Spring Data JPA   | Database access                |
| Hibernate         | ORM                            |
| Spring Security   | Authentication & authorization |
| JWT               | Stateless authentication       |
| MySQL             | Relational database            |
| Maven             | Dependency management & build  |
| Swagger / OpenAPI | API documentation              |
| Lombok            | Development utility            |

---

## 🏗️ Architecture

The application follows a layered architecture:

```text
Client
   │
   ▼
Controller Layer
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
MySQL Database
```

### Main layers

* **Controller** — Handles HTTP requests and responses
* **Service** — Contains business logic
* **Repository** — Handles database operations
* **Entity** — Represents database tables
* **DTO** — Controls API request/response data
* **Security** — Handles JWT authentication and authorization
* **Exception** — Provides centralized error handling
* **Config** — Contains security and OpenAPI configuration

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── shell/
│   │       └── cart/
│   │           └── shell_cart_backend/
│   │               ├── auth/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── dto/
│   │               ├── entity/
│   │               ├── exception/
│   │               ├── repository/
│   │               ├── security/
│   │               └── service/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── shell/
            └── cart/
                └── shell_cart_backend/
```

---

## 🔐 Authentication & Authorization

Shell-Cart uses **JWT-based stateless authentication**.

### Authentication flow

```text
User
 │
 │ Login with email + password
 ▼
AuthController
 │
 ▼
AuthService
 │
 │ Verify BCrypt password
 ▼
JwtService
 │
 │ Generate JWT
 ▼
Client
 │
 │ Authorization: Bearer <JWT>
 ▼
JwtAuthenticationFilter
 │
 ▼
Protected API
```

### Roles

The application currently supports:

* `USER`
* `ADMIN`

New users registering through the public registration endpoint are automatically assigned the `USER` role.

Administrative operations require the `ADMIN` role.

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint          | Access | Description                       |
| ------ | ----------------- | ------ | --------------------------------- |
| POST   | `/api/auth/login` | Public | Authenticate user and receive JWT |

### Users

| Method | Endpoint          | Access        | Description                                 |
| ------ | ----------------- | ------------- | ------------------------------------------- |
| POST   | `/api/users`      | Public        | Register a new user                         |
| GET    | `/api/users`      | ADMIN         | Get all users                               |
| GET    | `/api/users/{id}` | Authenticated | Get own profile / admin can access any user |
| DELETE | `/api/users/{id}` | ADMIN         | Delete a user                               |

### Products

| Method | Endpoint             | Access        | Description       |
| ------ | -------------------- | ------------- | ----------------- |
| POST   | `/api/products`      | ADMIN         | Create product    |
| GET    | `/api/products`      | Authenticated | Get all products  |
| GET    | `/api/products/{id}` | Authenticated | Get product by ID |
| PUT    | `/api/products/{id}` | ADMIN         | Update product    |
| DELETE | `/api/products/{id}` | ADMIN         | Delete product    |

### Cart

| Method | Endpoint                 | Access        | Description              |
| ------ | ------------------------ | ------------- | ------------------------ |
| POST   | `/api/cart`              | Authenticated | Add product to cart      |
| GET    | `/api/cart`              | Authenticated | View current user's cart |
| PUT    | `/api/cart/{cartItemId}` | Authenticated | Update cart quantity     |
| DELETE | `/api/cart/{cartItemId}` | Authenticated | Remove cart item         |
| DELETE | `/api/cart/clear`        | Authenticated | Clear cart               |

### Orders

| Method | Endpoint                      | Access        | Description                     |
| ------ | ----------------------------- | ------------- | ------------------------------- |
| POST   | `/api/orders/checkout`        | Authenticated | Create order from cart          |
| GET    | `/api/orders`                 | ADMIN         | Get all orders                  |
| GET    | `/api/orders/my-orders`       | Authenticated | Get current user's orders       |
| GET    | `/api/orders/{id}`            | Authenticated | Get order details               |
| GET    | `/api/orders/{orderId}/items` | Authenticated | Get items belonging to an order |

---

## 🛒 Order & Inventory Processing

Checkout is implemented as a transactional operation.

When a user checks out:

```text
1. Validate cart
       ↓
2. Lock products for update
       ↓
3. Validate available stock
       ↓
4. Calculate total amount
       ↓
5. Create Order
       ↓
6. Create OrderItems
       ↓
7. Reduce product stock
       ↓
8. Clear user's cart
       ↓
9. Commit transaction
```

The checkout operation uses Spring's `@Transactional` support.

Product rows are locked using a pessimistic write lock during checkout to help prevent inconsistent stock updates when multiple transactions attempt to purchase the same product.

Order items also store the product price at the time of purchase, preserving the historical purchase price even if the product price changes later.

---

## 🗄️ Database

The application uses **MySQL**.

Main database entities include:

```text
users
products
cart_items
orders
order_items
```

Relationships are represented using IDs in the current implementation.

---

## ⚙️ Configuration

The application uses environment variables for sensitive configuration.

Example:

```properties
spring.application.name=shell-cart-backend

spring.datasource.url=jdbc:mysql://localhost:3306/shell_cart
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.jpa.open-in-view=false

server.port=8080

jwt.secret=${JWT_SECRET}
```

### Required environment variables

```text
DB_PASSWORD
JWT_SECRET
```

Do not commit database passwords, JWT secrets, or other credentials to GitHub.

---

## 💻 Getting Started

### Prerequisites

Install the following:

* Java 21 or compatible Java runtime
* Maven
* MySQL
* Git

### 1. Clone the repository

```bash
git clone https://github.com/avdhootmisal1702-cmyk/shell-cart-backend.git
```

```bash
cd shell-cart-backend
```

### 2. Create the database

Open MySQL and run:

```sql
CREATE DATABASE shell_cart;
```

### 3. Configure environment variables

Set:

```text
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_long_random_secret
```

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend will start on:

```text
http://localhost:8080
```

---

## 📖 Swagger API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

### Using JWT with Swagger

1. Login using:

```text
POST /api/auth/login
```

2. Copy the returned JWT.

3. Click **Authorize** in Swagger UI.

4. Enter:

```text
Bearer YOUR_JWT_TOKEN
```

5. Click **Authorize**.

Protected endpoints can now be tested directly from Swagger UI.

---

## 🔒 Security

Security measures implemented in the application include:

* BCrypt password hashing
* JWT authentication
* Stateless security configuration
* Role-based authorization
* Protected administrative endpoints
* User ownership checks
* Request validation
* Centralized exception handling
* Secrets loaded through environment variables
* Registration endpoint prevents users from assigning themselves the `ADMIN` role

---

## 🧪 API Testing

The backend has been tested through:

* Swagger UI
* JWT authentication
* User registration
* User/Admin authorization
* Product CRUD
* Cart operations
* Checkout
* Inventory reduction
* Cart clearing after checkout
* User order history
* Order details
* Order items
* Access-control scenarios

---

## 📌 Example Authentication Request

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Request:

```json
{
  "email": "user@example.com",
  "password": "your-password"
}
```

The API returns a JWT token that can be supplied in the `Authorization` header:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 📌 Example Product Request

### Create Product

```http
POST /api/products
Authorization: Bearer <ADMIN_JWT>
Content-Type: application/json
```

Example request body:

```json
{
  "name": "Gaming Mouse",
  "description": "Wireless gaming mouse",
  "price": 1499.00,
  "quantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/mouse.jpg"
}
```

---

## 🎯 Project Goals

Shell-Cart is designed as a full-stack e-commerce application demonstrating practical backend development concepts including:

* REST API development
* Authentication and authorization
* Database design
* Secure password handling
* Transaction management
* Inventory management
* Concurrency handling
* API documentation
* Exception handling
* Clean layered architecture

---

## 🔮 Future Enhancements

Planned improvements include:

* React frontend
* Product search and filtering
* Pagination
* Category-based product browsing
* Order status management
* Payment gateway integration
* Email notifications
* Product reviews and ratings
* Automated testing expansion
* Docker support
* CI/CD pipeline
* AWS deployment

---

## 👨‍💻 Author

**Avdhoot Misal**

GitHub:
https://github.com/avdhootmisal1702-cmyk

---

## 📄 License

This project is currently intended as a personal/educational placement project.
