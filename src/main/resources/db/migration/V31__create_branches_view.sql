CREATE OR REPLACE VIEW branches_view AS
WITH ImportedQuantity AS (
    SELECT
        product.id AS product_id,
        pu.unit_id,
        i.branch_id,
        id.expiration_date,
        SUM(id.quantity) AS imported_quantity
    FROM
        product
            JOIN public.product_unit pu ON product.id = pu.product_id
            JOIN public.import_detail id ON id.product_id = product.id AND id.unit_id = pu.unit_id
            JOIN public.import i ON id.import_id = i.id
            JOIN public.branch b ON b.id = i.branch_id
    GROUP BY
        pu.unit_id, i.branch_id, product.id, id.expiration_date
),
     OrderedQuantity AS (
         SELECT
             oi.product_id,
             oi.unit_id,
             o.branch_id,
             oi.expiration_date AS expiration_date,
             SUM(oi.quantity) AS ordered_quantity
         FROM
             public.order_item oi
                 JOIN public.orders o ON o.id = oi.order_id
                 JOIN public.branch b ON b.id = o.branch_id
         WHERE
                 o.status NOT IN ('PENDING', 'CANCELLED')
         GROUP BY
             oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date
     )

SELECT
    io.product_id,
    io.unit_id,
    io.branch_id,
    io.expiration_date,
    io.imported_quantity - COALESCE(oo.ordered_quantity, 0) AS quantity
FROM
    ImportedQuantity io
        LEFT JOIN OrderedQuantity oo ON io.product_id = oo.product_id AND io.branch_id = oo.branch_id AND io.unit_id = oo.unit_id
WHERE
        io.expiration_date > CURRENT_DATE + INTERVAL '3 months';

SELECT *
FROM branches_view;