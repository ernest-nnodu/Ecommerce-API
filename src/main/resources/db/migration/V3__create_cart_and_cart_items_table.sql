CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY fk_carts_customers (customer_id)
                   REFERENCES customers (id)
                   ON UPDATE CASCADE
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY cart_items_carts (cart_id) REFERENCES carts(id),
    FOREIGN KEY cart_items_products (product_id) REFERENCES products(id)
);