# ECOMMERCE API

## GOAL

The goal of this project is to help you understand how to build a logic-heavy application with complex data models. You will also learn how to interact with external services and handle user authentication.

## REQUIREMENT

- Ability for users to sign up and log in.
- Ability to add products to a cart.
- Ability to remove products from a cart.
- Ability to view and search for products.
- Ability for users to check out and pay for products.
- You should also have some sort of admin panel where only you can add products, add product categories, set the prices, manage inventory, and more.


## 🧩User Stories (WHAT the system should do)

### 👤 Customer User Stories
#### 🔐 Authentication

- As a user, I want to register an account so that I can purchase products.
- As a user, I want to log in so that I can access my cart and orders.

#### 🛍️ Product Browsing

- As a user, I want to view available products so that I can decide what to buy.
- As a user, I want to search for products so that I can quickly find items.

#### 🛒 Cart Management

- As a user, I want to add products to my cart so that I can purchase them later.
- As a user, I want to remove products from my cart so that I can update my selection.
- As a user, I want to view my cart so that I can see selected items before checkout.

#### 💳 Checkout & Payment

- As a user, I want to checkout my cart so that I can place an order.
- As a user, I want to pay for my order so that my purchase is completed.

#### 📦 Orders

- As a user, I want to view my orders so that I can track my purchases.

### 🛠️ Admin User Stories
#### 📦 Product Management

- As an admin, I want to create products so that they can be sold.
- As an admin, I want to update product details so that information stays accurate.
- As an admin, I want to delete products so that I can remove unavailable items.

#### 🏷️ Category Management

- As an admin, I want to create categories so that products can be organised.

#### 📊 Inventory Management

- As an admin, I want to manage product inventory so that stock levels are accurate.

## 🧠 Use Cases (HOW the system behaves)

### 🔵 Use Case 1: Register User
**Actor: User**

**Main Flow**

- User submits email and password
- System validates input
- System checks if user already exists
- System encrypts password
- System saves user
- System returns success response

***Edge Cases***
- Email already exists
- Invalid email format
- Weak password

### 🔵 Use Case 2: View Products
**Actor: User**

**Main Flow**

- User requests product list
- System retrieves available products
- System returns product data

***Edge Cases***
- No products available

### 🔵 Use Case 3: Search Products
**Actor: User**

**Main Flow**

- User enters search query
- System searches products by:
  - name
  - category
- System returns matching results

***Edge Cases***
- No results found

### 🔵 Use Case 4: Add Product to Cart (CORE FLOW)
**Actor: Authenticated User**

**Main Flow**

- User selects product
- User specifies quantity
- System validates:
  - product exists
  - sufficient stock
- System adds item to cart
- System updates cart total
- System returns updated cart

***Edge Cases***
- Product not found
- Insufficient stock
- Invalid quantity

### 🔵 Use Case 5: Remove Product from Cart
**Actor: Authenticated User**

**Main Flow**

- User selects item in cart
- System removes item
- System updates cart total
- System returns updated cart

***Edge Cases***
- Item not found in cart

### 🔵 Use Case 6: Checkout
**Actor: Authenticated User**

**Main Flow**

- User initiates checkout
- System retrieves cart items
- System validates:
  - cart is not empty
  - stock availability
- System calculates total amount
- System creates order (status: PENDING)
- System proceeds to payment

***Edge Cases***
- Empty cart
- Out-of-stock items

### 🔵 Use Case 7: Payment Processing
**Actor: User / External Payment Service**

**Main Flow**

- System sends payment request to external service
- User completes payment
- Payment service returns result
- System updates order status:
  - SUCCESS → PAID
  - FAILURE → FAILED
- System reduces inventory
- System confirms order

***Edge Cases***
- Payment failure
- Timeout
- Service unavailable

### 🔵 Use Case 8: View Orders
**Actor: Authenticated User**

**Main Flow**

- User requests order history
- System retrieves user orders
- System returns order list

### 🔵 Use Case 9: Admin Creates Product
**Actor: Admin**

**Main Flow**

- Admin submits product details:
  - name
  - price
  - category
  - stock
- System validates input
- System saves product
- System returns created product

***Edge Cases***
- Invalid price
- Missing category

### 🔵 Use Case 10: Update Inventory
**Actor: Admin**

**Main Flow**

- Admin selects product
- Admin updates stock level
- System validates input
- System updates inventory
- System returns updated product