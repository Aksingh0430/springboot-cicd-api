-- ─────────────────────────────────────────────────────────────────────────────
-- V3: Add useful database view for stock reporting
-- Author: DevOps Engineer
-- ─────────────────────────────────────────────────────────────────────────────

-- Drop view if exists first (H2 compatible approach)
DROP VIEW IF EXISTS product_stock_summary;

-- Create stock summary view
CREATE VIEW product_stock_summary AS
SELECT
    id,
    name,
    category,
    price,
    quantity,
    CASE
        WHEN quantity = 0   THEN 'OUT_OF_STOCK'
        WHEN quantity <= 5  THEN 'LOW_STOCK'
        WHEN quantity <= 20 THEN 'IN_STOCK'
        ELSE                     'WELL_STOCKED'
    END AS stock_status,
    created_at,
    updated_at
FROM products;
