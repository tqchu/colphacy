ALTER TABLE orders
    ADD COLUMN admin_confirm_deliver bool;
ALTER TABLE orders
    ADD COLUMN admin_confirm_deliver_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;