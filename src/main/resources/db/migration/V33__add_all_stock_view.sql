ALTER VIEW stock_view RENAME TO available_stock_view;

CREATE OR REPLACE VIEW all_stock_view AS
WITH imported_quantity AS (SELECT pu.product_id,
                                  pu.unit_id,
                                  i.branch_id,
                                  id.expiration_date,
                                  SUM(id.quantity) AS imported_quantity
                           FROM import i
                                    JOIN import_detail id
                                         ON i.id = id.import_id
                                    JOIN product_unit pu
                                         ON pu.unit_id = id.unit_id
                                             AND pu.product_id = id.product_id
                                    JOIN branch b
                                         ON b.id = i.branch_id
                           GROUP BY pu.unit_id, i.branch_id, pu.product_id, id.expiration_date),
     sold_quantity AS (SELECT oi.product_id,
                              oi.unit_id,
                              o.branch_id,
                              oi.expiration_date AS expiration_date,
                              SUM(oi.quantity)   AS ordered_quantity
                       FROM orders o
                                JOIN order_item oi
                                     ON o.id = oi.order_id
                                JOIN product_unit pu
                                     ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                                JOIN branch b
                                     ON b.id = o.branch_id
                       WHERE o.status NOT IN ('CANCELLED')
                       GROUP BY oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date)

SELECT io.product_id,
       io.unit_id,
       io.branch_id,
       io.expiration_date,
       sum(io.imported_quantity - COALESCE(oo.ordered_quantity, 0)) AS quantity
FROM imported_quantity io
         LEFT JOIN sold_quantity oo
                   ON io.product_id = oo.product_id AND io.branch_id = oo.branch_id AND io.unit_id = oo.unit_id AND
                      io.expiration_date = oo.expiration_date
GROUP BY io.product_id, io.branch_id, io.unit_id, io.expiration_date
