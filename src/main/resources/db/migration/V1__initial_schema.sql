CREATE TABLE IF NOT EXISTS customer
(
    customer_id VARCHAR(36) PRIMARY KEY,
    customer_document VARCHAR(20),
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(255),
    customer_address VARCHAR(1000)
    );

CREATE TABLE IF NOT EXISTS "order"
(
    order_number SERIAL PRIMARY KEY,
    order_date DATE NOT NULL,
    order_customer_id CHAR(36),
    order_status TEXT NOT NULL,
    order_total NUMERIC(15,2) NOT NULL,
    CONSTRAINT fk_order_customer_id FOREIGN KEY(order_customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE IF NOT EXISTS product
(
    product_number SERIAL NOT NULL PRIMARY KEY,
    product_order_number INTEGER NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_category VARCHAR(255) NOT NULL,
    product_price NUMERIC(15,2) NOT NULL,
    product_description VARCHAR(255),
    product_min_sub_item INTEGER,
    product_max_sub_item INTEGER,
    CONSTRAINT fk_product_order_number FOREIGN KEY(product_order_number) REFERENCES "order"(order_number) ON DELETE CASCADE
);

