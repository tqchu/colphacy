-- Create a temporary table with your sets (product_id, unit_id, n)
DROP TABLE IF EXISTS sets;
CREATE TEMP TABLE sets
(
    product_id INT,
    unit_id    INT,
    n          INT
);

-- Insert your sets into the temporary table
INSERT INTO sets (product_id, unit_id, n)
VALUES (13, 1, 20),
       (14, 1, 30)
-- Add more sets as needed
;
WITH quantity_sum AS (SELECT t.product_id,
                             t.unit_id,
                             t.branch_id,
                             t.quantity,
                             SUM(t.quantity)
                             OVER (PARTITION BY t.product_id, t.unit_id, t.branch_id ORDER BY t.branch_id ASC, t.expiration_date ASC ROWS UNBOUNDED PRECEDING) as running_total,
                             t.expiration_date,
                             s.n
                      FROM branches_view t
                               JOIN
                           sets s ON t.product_id = s.product_id AND t.unit_id = s.unit_id),
     max_running_total AS (SELECT product_id,
                                  unit_id,
                                  branch_id,
                                  quantity,
                                  MAX(running_total) OVER (PARTITION BY product_id, unit_id, branch_id) as max_running_total
                           FROM quantity_sum)

SELECT qs.product_id,
       qs.unit_id,
       qs.branch_id,
       CASE
           WHEN qs.running_total < n THEN qs.quantity
           ELSE qs.n - (qs.running_total - qs.quantity)
           END as quantity,
       qs.expiration_date
FROM quantity_sum qs
         JOIN max_running_total mrt
              ON mrt.branch_id = qs.branch_id
                  AND mrt.product_id = qs.product_id
                  AND mrt.unit_id = qs.unit_id
                  AND mrt.quantity = qs.quantity
WHERE qs.branch_id =
      (SELECT qs.branch_id
       FROM quantity_sum qs
                JOIN max_running_total mrt
                     ON mrt.branch_id = qs.branch_id
                         AND mrt.product_id = qs.product_id
                         AND mrt.unit_id = qs.unit_id
                         AND mrt.quantity = qs.quantity
       WHERE mrt.max_running_total >= qs.n
         AND ((qs.n - (qs.running_total - qs.quantity)) > 0)
       GROUP BY qs.branch_id
       HAVING COUNT(*) = 2
       LIMIT 1)
  AND mrt.max_running_total >= qs.n
  AND ((qs.n - (qs.running_total - qs.quantity)) > 0)