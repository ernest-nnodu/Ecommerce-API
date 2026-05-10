CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_carts_customers FOREIGN KEY (customer_id)
                   REFERENCES customers (id)
                   ON UPDATE CASCADE
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT cart_items_products FOREIGN KEY (product_id) REFERENCES products(id)
);