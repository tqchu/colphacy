ALTER TABLE customer
    ADD COLUMN latitude  DECIMAL(9, 6) NOT NULL DEFAULT 16.07,
    ADD COLUMN longitude DECIMAL(9, 6) NOT NULL DEFAULT 108.15;