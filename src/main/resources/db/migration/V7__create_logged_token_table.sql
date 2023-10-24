CREATE TABLE logged_token
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    token          VARCHAR(255) NOT NULL
);

CREATE INDEX idx_logged_token ON logged_token (token);