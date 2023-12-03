SELECT
    r.id,
    r.rating,
    r.content,
    r.created_time,
    r.parent_review_id,
    c.full_name AS reviewer_name,
    e.full_name AS employee_name
FROM
    reviews r
        JOIN
    public.customer c ON c.id = r.customer_id
        LEFT JOIN
    public.employee e ON e.id = r.employee_id
WHERE
        r.product_id = :product_id
ORDER BY
    r.created_time;

select * from reviews;