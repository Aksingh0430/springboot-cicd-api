-- ─────────────────────────────────────────────────────────────────────────────
-- V1: Create products table
-- Author: DevOps Engineer
-- Date: 2024-01-01
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL UNIQUE,
    description VARCHAR(500),
    price       DECIMAL(10, 2)  NOT NULL CHECK (price > 0),
    quantity    INTEGER         NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    category    VARCHAR(100)    NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Index for faster category filtering (common query pattern)
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- Index for faster name search
CREATE INDEX IF NOT EXISTS idx_products_name ON products(LOWER(name));

-- Index for price range queries
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);

COMMENT ON TABLE products IS 'Stores product catalog information';
COMMENT ON COLUMN products.id IS 'Auto-generated primary key';
COMMENT ON COLUMN products.name IS 'Unique product name';
COMMENT ON COLUMN products.price IS 'Product price - must be > 0';
COMMENT ON COLUMN products.quantity IS 'Stock quantity - cannot be negative';
