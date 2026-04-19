# Ecommerce-API

A RESTful API for an e-commerce platform built with Spring Boot, enabling customers to browse products, manage shopping carts, place orders, and process payments securely.

## Features

- **User Authentication & Authorization**: JWT-based login with role-based access (customer/admin).
- **Product Catalog**: Manage categories and products with full CRUD operations for admins.
- **Shopping Cart**: Add, update, remove items, and view cart contents.
- **Order Management**: Checkout process, order history, and payment integration via Stripe.
- **Admin Dashboard**: Oversight of customers, orders, and catalog management.
- **Payment Processing**: Secure payments with Stripe webhooks for real-time updates.
- **Database Management**: Automated schema migrations with Flyway.
- **Monitoring**: Health checks and metrics via Spring Boot Actuator.

## Technologies Used

- **Backend**: Java 21, Spring Boot 4.0.4
- **Database**: MySQL with JPA and Flyway migrations
- **Security**: Spring Security, JWT (JJWT library)
- **Payments**: Stripe API
- **Build Tool**: Maven
- **Other**: Lombok, MapStruct, Bean Validation, dotenv for environment variables

## Prerequisites

- Java 21 or higher
- MySQL Server (running on localhost:3306)
- Maven 3.6+
- Environment variables: `JWT_SECRET`, `STRIPE_SECRET`, `STRIPE_WEBHOOK_SECRET`

## Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/ecommerce-api.git
   cd ecommerce-api
   ```

2. **Set up the database**:
   - Ensure MySQL is running.
   - The application will create the `ecommerce` database automatically if it doesn't exist (configured in `application.yml`).

3. **Configure environment variables**:
   - Create a `.env` file in the root directory or set system environment variables:
     ```
     JWT_SECRET=your-jwt-secret-key
     STRIPE_SECRET=your-stripe-secret-key
     STRIPE_WEBHOOK_SECRET=your-stripe-webhook-secret
     ```

4. **Run the application**:
   - Using Maven wrapper:
     ```bash
     ./mvnw spring-boot:run
     ```
   - Or with Maven:
     ```bash
     mvn spring-boot:run
     ```
   - The API will start on `http://localhost:8080`.

5. **Run database migrations** (if needed):
   - Flyway runs automatically on startup, but you can trigger manually:
     ```bash
     ./mvnw flyway:migrate
     ```

## Usage

### API Endpoints Overview

#### Authentication
- `POST /auth/login` - User login (returns JWT access token)
- `POST /auth/refresh` - Refresh access token
- `GET /auth/current-user` - Get current authenticated user

#### Customers
- `POST /customers` - Register a new customer
- `GET /customers/me` - Get current customer profile
- `PUT /customers/me` - Update customer profile

#### Products & Categories
- `GET /products` - List all products
- `GET /products/{id}` - Get product by ID
- `GET /categories` - List all categories

#### Cart
- `POST /customers/carts/items` - Add item to cart
- `GET /customers/carts` - Get cart contents
- `PUT /customers/carts/items/{id}` - Update cart item
- `DELETE /customers/carts/items/{id}` - Remove item from cart
- `DELETE /customers/carts/items` - Clear cart

#### Orders
- `POST /customers/orders` - Checkout and create order
- `POST /customers/orders/checkout-webhook` - Handle Stripe payment webhooks
- `GET /customers/orders` - Get user's orders
- `GET /customers/orders/{id}` - Get order by ID

#### Admin (Requires admin role)
- `GET /admin/customers` - List all customers
- `GET /admin/customers/{id}` - Get customer by ID
- `POST /admin/categories` - Create category
- `PUT /admin/categories/{id}` - Update category
- `POST /admin/products` - Create product
- `PUT /admin/products/{id}` - Update product
- `DELETE /admin/products/{id}` - Delete product
- `GET /admin/orders` - List all orders
- `GET /admin/orders/{id}` - Get order by ID

### Testing the API
- Use tools like Postman or curl to interact with endpoints.
- Example login request:
  ```bash
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email": "user@example.com", "password": "password"}'
  ```
- Include the JWT token in the `Authorization: Bearer <token>` header for protected routes.

## Database Schema

The database schema is managed via Flyway migrations in `src/main/resources/db/migration/`. Key tables include:
- `customer` - User accounts with roles
- `product` - Product details
- `category` - Product categories
- `cart` & `cart_items` - Shopping cart data
- `order` & `order_items` - Order and line items
- `payment` - Payment records with statuses

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with Spring Boot for rapid development.
- Payment integration powered by Stripe.
- Inspired by standard e-commerce API patterns.
