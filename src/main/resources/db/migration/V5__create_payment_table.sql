CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    date DATETIME NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY fk_payments_orders (order_id) REFERENCES orders(id)
);