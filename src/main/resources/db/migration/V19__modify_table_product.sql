ALTER TABLE product
    ADD short_description VARCHAR(255);

ALTER TABLE product
    ALTER COLUMN indications DROP NOT NULL;

ALTER TABLE product
    ALTER COLUMN notes DROP NOT NULL;

ALTER TABLE product
    ALTER COLUMN side_effects DROP NOT NULL;