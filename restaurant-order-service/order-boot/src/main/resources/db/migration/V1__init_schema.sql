CREATE SCHEMA IF NOT EXISTS order_schema;

-- Create orders table
CREATE TABLE IF NOT EXISTS order_schema.orders (
    id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    tax NUMERIC(10,2) NOT NULL,
    total NUMERIC(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    notes TEXT
    );

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_schema.order_items (
    id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    total_price NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES order_schema.orders(id)
    );

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON order_schema.orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON order_schema.orders(status);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_schema.order_items(order_id);