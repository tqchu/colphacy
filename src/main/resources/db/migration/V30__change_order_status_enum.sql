-- Create a new ENUM type
CREATE TYPE order_status_new AS ENUM ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED');
-- Remove the default value
ALTER TABLE orders
    ALTER COLUMN status DROP DEFAULT;

-- Change the column type
ALTER TABLE orders
    ALTER COLUMN status TYPE order_status_new USING status::text::order_status_new;

-- Set the default value again, replace 'your_default_value' with your actual default value
ALTER TABLE orders
    ALTER COLUMN status SET DEFAULT 'PENDING';

-- Now you can drop the old type and rename the new type
DROP TYPE order_status;
ALTER TYPE order_status_new RENAME TO order_status;
