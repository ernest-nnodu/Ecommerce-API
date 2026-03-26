CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(10, 2) NOT NULL,
    date DATETIME NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY orders_customers (customer_id)
                    REFERENCES customers(id)
                    ON UPDATE CASCADE
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    price DECIMAL(5, 2) NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY order_items_orders (order_id) REFERENCES orders(id),
    FOREIGN KEY order_items_products (product_id) REFERENCES products(id)
);