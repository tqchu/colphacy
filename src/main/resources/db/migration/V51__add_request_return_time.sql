ALTER TABLE orders
    ADD COLUMN request_return_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE orders
    ALTER COLUMN resolve_type SET DEFAULT 'PENDING';