SELECT
    r.id,
    p.id as product_id,
    p.name,
    pi.url,
    r.rating,
    r.content,
    r.created_time,
    r.parent_review_id,
    c.full_name AS reviewer_name,
    c.phone AS customer_phone
FROM
    reviews r
        JOIN
    public.product p on r.product_id = p.id
        JOIN
    public.product_image pi on pi.product_id = p.id
        JOIN
    public.customer c ON c.id = r.customer_id
WHERE
        r.product_id = :product_id
ORDER BY
    r.created_time;