-- ─────────────────────────────────────────────────────────────────────────────
-- V2: Insert seed/sample data
-- Author: DevOps Engineer
-- Date: 2024-01-02
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO products (name, description, price, quantity, category) VALUES
    ('MacBook Pro 14',       'Apple M3 Pro chip, 18GB RAM, 512GB SSD',          1999.99, 10, 'Electronics'),
    ('iPhone 15 Pro',        'Apple A17 Pro chip, 256GB, Titanium finish',       1099.99, 25, 'Electronics'),
    ('Samsung Galaxy S24',   'Snapdragon 8 Gen 3, 128GB, 50MP camera',           849.99,  30, 'Electronics'),
    ('Dell XPS 15',          'Intel Core i9, 32GB RAM, OLED display',            1799.99,  8, 'Electronics'),
    ('Mechanical Keyboard',  'Cherry MX Red switches, TKL layout, RGB backlit',  129.99,  20, 'Electronics'),
    ('4K UHD Monitor 27"',   'IPS panel, 144Hz, HDR400, USB-C hub',              379.99,  15, 'Electronics'),
    ('Effective Java',       'Effective Java 3rd Edition by Joshua Bloch',         49.99, 100, 'Books'),
    ('Clean Code',           'A Handbook of Agile Software Craftsmanship',         39.99,  75, 'Books'),
    ('System Design Interview','Vol 1 & 2 - Alex Xu',                             54.99,  60, 'Books'),
    ('Spring Boot in Action','Craig Walls - Spring Boot 3 edition',               44.99,  80, 'Books'),
    ('Standing Desk Electric','Height adjustable 140cm x 70cm, memory presets',  599.99,   5, 'Furniture'),
    ('Ergonomic Chair',      'Lumbar support, mesh back, adjustable armrests',   449.99,   8, 'Furniture'),
    ('Laptop Stand',         'Aluminium, adjustable height, foldable',            49.99,  40, 'Accessories'),
    ('USB-C Hub 10-in-1',   '4K HDMI, 100W PD, SD card, Gigabit Ethernet',       69.99,  35, 'Accessories'),
    ('Blue Light Glasses',   'Anti-glare, UV400 protection, lightweight frame',   29.99,  50, 'Accessories')
ON CONFLICT (name) DO NOTHING;
