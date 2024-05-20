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
    order_ordered_at TIMESTAMP NOT NULL,
    order_customer_id CHAR(36),
    order_status TEXT NOT NULL,
    order_total NUMERIC(15,2) NOT NULL,
    CONSTRAINT fk_order_customer_id FOREIGN KEY(order_customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE IF NOT EXISTS order_line
(
    order_line_number SERIAL NOT NULL PRIMARY KEY,
    order_line_order_number INTEGER NOT NULL,
    order_line_product_number INTEGER NOT NULL,
    order_line_name VARCHAR(255) NOT NULL,
    order_line_description VARCHAR(255) NOT NULL,
    order_line_unit_price NUMERIC(15,2) NOT NULL,
    order_line_quantity INTEGER,
    order_line_total NUMERIC(15,2) NOT NULL,
    CONSTRAINT fk_order_line_order_number FOREIGN KEY(order_line_order_number) REFERENCES "order"(order_number) ON DELETE CASCADE
);
