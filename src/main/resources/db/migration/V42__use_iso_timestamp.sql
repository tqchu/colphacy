-- Alter the import table
ALTER TABLE import
    ALTER COLUMN import_time TYPE TIMESTAMP WITH TIME ZONE USING import_time AT TIME ZONE 'UTC';

-- Alter the orders table
ALTER TABLE orders
    ALTER COLUMN order_time TYPE TIMESTAMP WITH TIME ZONE USING order_time AT TIME ZONE 'UTC',
    ALTER COLUMN confirm_time TYPE TIMESTAMP WITH TIME ZONE USING confirm_time AT TIME ZONE 'UTC',
    ALTER COLUMN ship_time TYPE TIMESTAMP WITH TIME ZONE USING ship_time AT TIME ZONE 'UTC',
    ALTER COLUMN deliver_time TYPE TIMESTAMP WITH TIME ZONE USING deliver_time AT TIME ZONE 'UTC',
    ALTER COLUMN cancel_time TYPE TIMESTAMP WITH TIME ZONE USING cancel_time AT TIME ZONE 'UTC';

-- Alter the receiver table
ALTER TABLE receiver
    ALTER COLUMN deleted_at TYPE TIMESTAMP WITH TIME ZONE USING deleted_at AT TIME ZONE 'UTC';

-- Alter the reviews table
ALTER TABLE reviews
    ALTER COLUMN created_time TYPE TIMESTAMP WITH TIME ZONE USING created_time AT TIME ZONE 'UTC';