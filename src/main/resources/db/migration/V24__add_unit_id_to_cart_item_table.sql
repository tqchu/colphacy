ALTER TABLE cart_item
    ADD COLUMN unit_id BIGINT NOT NULL,
    ADD FOREIGN KEY(unit_id) REFERENCES unit(id);