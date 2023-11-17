ALTER TABLE import_detail
    ADD COLUMN unit_id BIGINT;
ALTER TABLE import_detail
    ADD FOREIGN KEY (unit_id) REFERENCES unit (id);