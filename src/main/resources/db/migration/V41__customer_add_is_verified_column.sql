ALTER TABLE customer
    ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT FALSE;
UPDATE customer
    SET is_verified = true;