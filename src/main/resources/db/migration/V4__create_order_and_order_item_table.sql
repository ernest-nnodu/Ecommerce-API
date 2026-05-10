CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(10, 2) NOT NULL,
    date DATETIME NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT orders_customers FOREIGN KEY (customer_id)
                    REFERENCES customers(id)
                    ON UPDATE CASCADE
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    price DECIMAL(5, 2) NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT order_items_orders FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT order_items_products FOREIGN KEY (product_id) REFERENCES products(id)
);