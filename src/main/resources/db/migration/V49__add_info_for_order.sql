-- create new type
CREATE TYPE cancel_type AS ENUM ('EMPLOYEE', 'CUSTOMER', 'UNPAID');
CREATE TYPE resolve_type AS ENUM ('REFUSED', 'RETURN', 'REFUND');

-- migrate from completed to delivered
UPDATE orders
SET status = 'DELIVERED'
WHERE status = 'COMPLETED';

-- drop completed and add returned to order_status
ALTER TYPE order_status
    RENAME VALUE 'COMPLETED' TO 'RETURNED';

-- add columns
ALTER TABLE orders
    ADD COLUMN cancel_by     cancel_type,
    ADD COLUMN cancel_return boolean,
    ADD COLUMN resolve_type  resolve_type,
    ADD COLUMN resolve_time  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;