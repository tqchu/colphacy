ALTER TABLE receiver
    ADD COLUMN branch_id BIGINT;

ALTER TABLE receiver
    ADD CONSTRAINT fk_branch
        FOREIGN KEY (branch_id) REFERENCES branch (id);