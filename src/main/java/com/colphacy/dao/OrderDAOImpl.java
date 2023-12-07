package com.colphacy.dao;

import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.order.OrderListViewCustomerDTO;
import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;
import com.colphacy.dto.product.ProductOrderItem;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria) {
        // Get the native SQL query
        String sql = getPaginatedOrdersByCriteria(criteria);

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        // Set the parameters based on the criteria
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;

        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (hasStartDateCondition) {
            query.setParameter("startDate", criteria.getStartDate());
        }
        if (hasEndDateCondition) {
            query.setParameter("endDate", criteria.getEndDate().atTime(23, 59, 59));
        }
        if (hasBranchIdCondition) {
            query.setParameter("branchId", criteria.getBranchId());
        }
        query.setParameter("status", criteria.getStatus().name());
        query.setParameter("limit", criteria.getLimit());
        query.setParameter("offset", criteria.getOffset());

        // Execute the query and return the result list, with each result transformed into an OrderListViewDTO object
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(OrderListViewDTO.class))
                .getResultList();
    }

    @Override
    public List<OrderListViewCustomerDTO> getPaginatedOrdersForCustomer(OrderSearchCriteria criteria) {
        // Get the native SQL query
        String sql = getPaginatedOrdersByCriteriaForCustomer(criteria);

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        // Set the parameters based on the criteria
        boolean hasKeywordCondition = criteria.getKeyword() != null;

        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }

        query.setParameter("customerId", criteria.getCustomerId());
        if (criteria.getStatus() != null) {
            query.setParameter("status", criteria.getStatus().name());
        }
        query.setParameter("limit", criteria.getLimit());
        query.setParameter("offset", criteria.getOffset());

        // Execute the query and return the result list, with each result transformed into an OrderListViewDTO object
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(OrderListViewCustomerDTO.class))
                .getResultList();
    }

    private String getPaginatedOrdersByCriteria(OrderSearchCriteria criteria) {
        String sql;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        if (hasKeywordCondition) {
            sql = """
                    WITH filtered_orders AS (SELECT o.id
                       FROM orders o
                                  JOIN order_item id ON o.id = id.order_id
                                  JOIN product p ON p.id = id.product_id
                                  JOIN customer c ON c.id = o.customer_id
                                  JOIN receiver r on r.id = o.receiver_id
                       WHERE unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                          OR unaccent(lower(c.full_name)) LIKE unaccent(lower('%' || :keyword || '%'))
                          OR c.phone LIKE '%' || :keyword || '%'
                          OR r.phone LIKE '%' || :keyword || '%')
                    SELECT o.id                     as id,
                           c.full_name              as customer,
                           o.order_time             as order_time,
                           o.ship_time              as ship_time,
                           o.confirm_time           as confirm_time,
                           o.deliver_time           as deliver_time,
                           o.cancel_time            as cancel_time,
                           SUM(od.price * quantity) as total
                    FROM orders o
                             JOIN order_item od ON o.id = od.order_id
                             JOIN customer c ON c.id = o.customer_id
                    WHERE o.id IN (SELECT id FROM filtered_orders)
                    """;
        } else {
            sql = """
                    SELECT o.id  as id,
                           c.full_name              as customer,
                           o.order_time             as order_time,
                           o.ship_time              as ship_time,
                           o.confirm_time           as confirm_time,
                           o.deliver_time           as deliver_time,
                           o.cancel_time            as cancel_time,
                           SUM(od.price * quantity) as total
                    FROM orders o
                             JOIN order_item od ON o.id = od.order_id
                             JOIN customer c ON c.id = o.customer_id
                    """;
        }

        // Add the date range and branch id conditions if they are present
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;
        sql += hasKeywordCondition ? " AND " : " WHERE ";
        sql += "status = :status";

        if (hasStartDateCondition || hasEndDateCondition || hasBranchIdCondition) {
            sql += " AND ";
        }

        if (hasStartDateCondition) {
            sql += "order_time >= :startDate";
            if (hasEndDateCondition || hasBranchIdCondition) {
                sql += " AND ";
            }
        }
        if (hasEndDateCondition) {
            sql += " order_time <= :endDate ";
            if (hasBranchIdCondition) {
                sql += " AND ";
            }
        }
        if (hasBranchIdCondition) {
            sql += "o.branch_id = :branchId";
        }

        // Add the grouping and ordering clauses
        sql += " GROUP BY o.id, c.id, o.order_time ORDER BY " + criteria.getSortBy() + " " + criteria.getOrder() + " LIMIT :limit OFFSET :offset";
        return sql;
    }

    private String getPaginatedOrdersByCriteriaForCustomer(OrderSearchCriteria criteria) {

        String sql = """
                WITH first_products AS (SELECT DISTINCT  ON (o.id)
                                                o.id as order_id,
                                                p.id,
                                                p.name,
                                                od.price,
                                                od.quantity,
                                                f_pi.url
                                     FROM orders o
                                              JOIN customer c ON c.id = o.customer_id AND customer_id = :customerId
                                              JOIN order_item od
                                                   ON od.order_id = o.id
                                              JOIN product p ON p.id = od.product_id
                                              """;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        if (hasKeywordCondition) {
            sql += " AND unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%')) ";
        }
        sql += """
                JOIN (SELECT MIN(url) as url, product_id
                      FROM product_image
                      GROUP BY product_id) f_pi
                     ON f_pi.product_id = p.id)
                SELECT o.id                        as id,
                       fp.id                       as product_id,
                       fp.name                     as product_name,
                       fp.price                    as product_price,
                       fp.quantity                 as product_quantity,
                       fp.url                      as product_image,
                       o.order_time                as order_time,
                       o.ship_time                 as ship_time,
                       o.confirm_time              as confirm_time,
                       o.deliver_time              as deliver_time,
                       o.cancel_time               as cancel_time,
                       o.status                    as status,
                       COUNT(od.order_id)          as total_items,
                       SUM(od.price * od.quantity) as total
                FROM orders o
                         JOIN customer c ON c.id = o.customer_id AND customer_id = :customerId
                         JOIN order_item od
                              ON od.order_id = o.id
                         JOIN first_products fp
                              ON fp.order_id = o.id
                """;
        // Add the date range and branch id conditions if they are present
        if (criteria.getStatus() != null) {
            sql += " WHERE o.status = :status ";
        }

        // Add the grouping and ordering clauses
        sql += " GROUP BY o.id, fp.id, fp.price, fp.name, fp.quantity, fp.url, o.order_time, o.ship_time, o.confirm_time, o.deliver_time, o.cancel_time, o.status " +
                " ORDER BY " + criteria.getSortBy() + " " + criteria.getOrder() + " LIMIT :limit OFFSET :offset";
        return sql;
    }

    private void createAndInsertTempTable(List<CartItemDTO> sets) {
        entityManager.createNativeQuery(
                        """
                                CREATE TEMPORARY TABLE IF NOT EXISTS temp_sets(
                                        product_id INT,
                                        unit_id INT,
                                        quantity INT
                                );
                                TRUNCATE temp_sets;
                                """
                )
                .executeUpdate();

        for (CartItemDTO set : sets) {
            entityManager.createNativeQuery("INSERT INTO temp_sets (product_id, unit_id, quantity) VALUES (?, ?, ?)")
                    .setParameter(1, set.getProductId())
                    .setParameter(2, set.getUnitId())
                    .setParameter(3, set.getQuantity())
                    .executeUpdate();
        }
    }

    @Override
    public List<ProductOrderItem> findAvailableProducts(List<CartItemDTO> items, double receiverLat, double receiverLong) {
        createAndInsertTempTable(items);
        String queryString = """
                WITH quantity_sum AS(SELECT t.product_id,
                        t.unit_id,
                        t.branch_id,
                        t.quantity,
                        SUM(t.quantity)
                        OVER(PARTITION BY t.product_id, t.unit_id, t.branch_id ORDER BY t.branch_id ASC, t.expiration_date ASC ROWS UNBOUNDED PRECEDING)as running_total,
                        t.expiration_date,
                        s.quantity as n
                        FROM available_stock_view t
                        JOIN temp_sets s ON t.product_id = s.product_id AND t.unit_id = s.unit_id),
                        max_running_total AS(SELECT product_id,
                        unit_id,
                        branch_id,
                        quantity,
                        MAX(running_total)OVER(PARTITION BY product_id, unit_id, branch_id)as max_running_total
                        FROM quantity_sum)
                SELECT qs.product_id as product_id,
                        qs.unit_id as unit_id,
                qs.branch_id as branch_id,
                        CASE
                WHEN qs.running_total<qs.n THEN qs.quantity
                ELSE qs.n - (qs.running_total - qs.quantity) END as quantity,
                        qs.expiration_date as expiration_date,
                pu.sale_price as price,
                        pu.ratio as ratio
                FROM quantity_sum qs
                JOIN max_running_total mrt
                ON mrt.branch_id = qs.branch_id
                AND mrt.product_id = qs.product_id
                AND mrt.unit_id = qs.unit_id
                AND mrt.quantity = qs.quantity
                JOIN product_unit pu
                ON qs.product_id = pu.product_id AND qs.unit_id = pu.unit_id
                WHERE qs.branch_id = (SELECT qs.branch_id
                FROM quantity_sum qs
                JOIN max_running_total mrt
                ON mrt.branch_id = qs.branch_id
                AND mrt.product_id = qs.product_id
                AND mrt.unit_id = qs.unit_id
                AND mrt.quantity = qs.quantity
                JOIN public.branch b ON qs.branch_id = b.id
                WHERE mrt.max_running_total >= qs.n
                AND((qs.n - (qs.running_total - qs.quantity)) > 0)
                GROUP BY qs.branch_id, b.latitude, b.longitude
                HAVING COUNT (distinct(qs.product_id, qs.unit_id, qs.branch_id)) = :size
                ORDER BY acos(cos(radians(:receiverLat)) *cos(radians(b.latitude))
                        * cos(radians(b.longitude) - radians(:receiverLong))+
                        sin(radians(:receiverLat))
                                                                        *sin(radians(b.latitude)))
                LIMIT 1)
                AND mrt.max_running_total >= qs.n
                AND((qs.n - (qs.running_total - qs.quantity)) > 0)
                """;

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("size", items.size());
        query.setParameter("receiverLat", receiverLat);
        query.setParameter("receiverLong", receiverLong);

        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductOrderItem.class))
                .getResultList();
    }

    @Override
    public List<ProductOrderItem> findAvailableProductsForABranch(List<CartItemDTO> items, Long branchId) {
        createAndInsertTempTable(items);
        String queryString = """
                WITH quantity_sum AS
                        (SELECT t.product_id,
                                t.unit_id,
                                t.quantity,
                                SUM(t.quantity)
                                OVER(PARTITION BY t.product_id, t.unit_id ORDER BY t.expiration_date ASC ROWS UNBOUNDED PRECEDING)as running_total,
                                t.expiration_date,
                                s.quantity as n
                        FROM available_stock_view t
                             JOIN temp_sets s ON t.product_id = s.product_id AND t.unit_id = s.unit_id AND t.branch_id = :branchId),
                    max_running_total AS
                        (SELECT product_id,
                                unit_id,
                                quantity,
                                MAX(running_total)OVER(PARTITION BY product_id, unit_id) as max_running_total
                        FROM quantity_sum)
                SELECT qs.product_id as product_id,
                          qs.unit_id as unit_id,
                       :branchId as branch_id,
                       CASE
                           WHEN qs.running_total<qs.n THEN qs.quantity
                           ELSE qs.n - (qs.running_total - qs.quantity) END as quantity,
                       qs.expiration_date as expiration_date,
                       pu.sale_price as price,
                       pu.ratio as ratio
                FROM quantity_sum qs
                         JOIN max_running_total mrt
                              ON  mrt.product_id = qs.product_id
                                  AND mrt.unit_id = qs.unit_id
                                  AND mrt.quantity = qs.quantity
                         JOIN product_unit pu
                              ON qs.product_id = pu.product_id AND qs.unit_id = pu.unit_id
                WHERE mrt.max_running_total >= qs.n
                  AND((qs.n - (qs.running_total - qs.quantity)) > 0)
                """;

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("branchId", branchId);

        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductOrderItem.class))
                .getResultList();
    }

    @Override
    public Long getTotalOrdersForCustomer(OrderSearchCriteria criteria) {
        String sql = getCountOrdersForCustomerQuery(criteria);

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("customerId", criteria.getCustomerId());

        if (criteria.getKeyword() != null) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (criteria.getStatus() != null) {
            query.setParameter("status", criteria.getStatus().name());
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public Long getTotalOrders(OrderSearchCriteria criteria) {
        String sql = getCountOrdersQuery(criteria);

        Query query = entityManager.createNativeQuery(sql);

        if (criteria.getKeyword() != null) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (criteria.getStartDate() != null) {
            query.setParameter("startDate", criteria.getStartDate());

        }
        if (criteria.getEndDate() != null) {
            query.setParameter("endDate", criteria.getEndDate());

        }
        if (criteria.getBranchId() != null) {
            query.setParameter("branchId", criteria.getBranchId());

        }
        query.setParameter("status", criteria.getStatus().name());

        return ((Number) query.getSingleResult()).longValue();
    }

    private String getCountOrdersQuery(OrderSearchCriteria criteria) {
        String sql =
                """
                        SELECT COUNT(*)
                        FROM (SELECT DISTINCT o.id
                              FROM orders o
                                       JOIN order_item id ON o.id = id.order_id
                                       JOIN customer c ON c.id = o.customer_id
                                       JOIN product p ON p.id = id.product_id
                                       JOIN receiver r ON r.id = o.receiver_id  
                                       """;
        if (criteria.getKeyword() != null) {
            sql += """
                    AND (unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                         OR unaccent(lower(c.full_name)) LIKE unaccent(lower('%' || :keyword || '%'))
                         OR c.phone LIKE '%' || :keyword || '%'
                         OR r.phone LIKE '%' || :keyword || '%')
                    """;
        }
        sql +=
                """ 
                        WHERE o.status = :status
                        """;
        if (criteria.getStartDate() != null) {
            sql += " AND order_time >= :startDate ";
        }
        if (criteria.getEndDate() != null) {
            sql += " AND order_time <= :endDate ";
        }
        if (criteria.getBranchId() != null) {
            sql += " AND o.branch_id = :branchId ";
        }
        sql += ")AS temp";
        return sql;

    }

    private String getCountOrdersForCustomerQuery(OrderSearchCriteria criteria) {
        String sql =
                """
                        SELECT COUNT(*)
                            FROM (SELECT DISTINCT o.id
                                  FROM orders o
                                           JOIN customer c ON c.id = o.customer_id AND customer_id = :customerId
                                           JOIN order_item od
                                                ON od.order_id = o.id
                                           JOIN product p
                                                ON p.id = od.product_id
                                     """;
        if (criteria.getKeyword() != null) {
            sql += "AND unaccent (lower(p.name)) LIKE unaccent (lower('%' || :keyword || '%'))";
        }
        if (criteria.getStatus() != null) {
            sql += " WHERE o.status = :status";
        }
        sql += ") as temp";
        return sql;
    }
}
