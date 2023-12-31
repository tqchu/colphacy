CREATE TYPE payment_method AS ENUM ('ON_DELIVERY', 'ONLINE');

ALTER TABLE orders
    ADD COLUMN payment_method payment_method           NOT NULL DEFAULT 'ON_DELIVERY',
    ADD COLUMN paid           BOOLEAN                  NOT NULL DEFAULT false,
    ADD COLUMN pay_time       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE OR REPLACE FUNCTION orders_paid_on_delivery()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.payment_method = 'ON_DELIVERY' THEN
        NEW.paid := true;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER orders_paid_on_delivery_trigger
    BEFORE INSERT OR UPDATE
    ON orders
    FOR EACH ROW
EXECUTE PROCEDURE orders_paid_on_delivery();

