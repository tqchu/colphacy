WITH imported_quantity AS (SELECT pu.product_id,
                                  pu.unit_id,
                                  id.expiration_date,
                                  sum(id.quantity) AS imported_quantity
                           FROM import i
                                    JOIN import_detail id ON i.id = id.import_id
                                    JOIN product_unit pu ON pu.unit_id = id.unit_id AND pu.product_id = id.product_id
                               --        TODO                      add keyword condition
                                    JOIN product p ON p.id = id.product_id
                               AND unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))

                           WHERE i.branch_id = :branchId

                           GROUP BY pu.unit_id, pu.product_id, id.expiration_date),
     sold_quantity AS (SELECT oi.product_id,
                              oi.unit_id,
                              oi.expiration_date,
                              sum(oi.quantity) AS ordered_quantity
                       FROM orders o
                                JOIN order_item oi ON o.id = oi.order_id
                                JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                           --   add keyword condition
                                JOIN product p ON p.id = oi.product_id
                           AND unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                       WHERE o.branch_id = :branchId
                         AND o.status <> 'CANCELLED'::order_status
                       GROUP BY oi.product_id, oi.unit_id, oi.expiration_date)
SELECT io.product_id                                                        as product_id,
       io.unit_id                                                           as unit_id,
       p.name                                                               as product_name,
       u.name                                                               as unit_name,
       io.expiration_date                                                   as expiration_date,
       sum(io.imported_quantity - COALESCE(oo.ordered_quantity, 0::bigint)) AS quantity
FROM ((SELECT io.product_id as product_id
       FROM imported_quantity io
                LEFT JOIN sold_quantity oo
                          ON io.product_id = oo.product_id AND io.unit_id = oo.unit_id AND
                             io.expiration_date = oo.expiration_date
                JOIN product p
                     ON p.id = io.product_id
                JOIN unit u
                     ON u.id = io.unit_id
       GROUP BY io.product_id
       -- TODO: sort by id by default
       ORDER BY sum(io.imported_quantity - COALESCE(oo.ordered_quantity, 0::bigint)) ASC
       -- TODO: limit, offset
       LIMIT 10 OFFSET 0)) as filtered_products
         JOIN imported_quantity io
              ON filtered_products.product_id = io.product_id
         LEFT JOIN sold_quantity oo
                   ON io.product_id = oo.product_id AND io.unit_id = oo.unit_id AND
                      io.expiration_date = oo.expiration_date
         JOIN product p
              ON p.id = io.product_id
         JOIN unit u
              ON u.id = io.unit_id
GROUP BY io.product_id, io.unit_id, io.expiration_date, p.name, u.name;