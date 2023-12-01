INSERT INTO reviews(
    content, rating, customer_id, product_id
)
VALUES ('Not bad', 1, 5, 2);

-- admin reply
INSERT INTO reviews(
    content, customer_id, product_id, parent_review_id, employee_id
)
VALUES ('Not bad', 5, 2, 1, 1);