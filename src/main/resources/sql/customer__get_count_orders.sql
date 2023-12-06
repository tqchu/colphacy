SELECT COUNT(*)
FROM (SELECT DISTINCT o.id
      FROM orders o
               JOIN customer c ON c.id = o.customer_id AND customer_id = :customerId
               JOIN order_item od
                    ON od.order_id = o.id
               JOIN product p
                    ON p.id = od.product_id
                        AND unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
      WHERE o.status = :status) as temp