CREATE TABLE category
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name           VARCHAR(50)                             NOT NULL UNIQUE
);