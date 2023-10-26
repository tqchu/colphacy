ALTER TABLE public.branch
    ADD COLUMN latitude  DECIMAL(9, 6) NOT NULL DEFAULT 16.07,
    ADD COLUMN longitude DECIMAL(9, 6) NOT NULL DEFAULT 108.15;

ALTER TABLE public.branch
    DROP CONSTRAINT IF EXISTS branch_phone_number_key;