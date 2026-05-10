CREATE TABLE IF NOT EXISTS categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(5, 2) NOT NULL,
    stock_quantity BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_products_categories
    FOREIGN KEY (category_id)
                      REFERENCES categories (id)
                      ON UPDATE CASCADE
                      ON DELETE NO ACTION
);