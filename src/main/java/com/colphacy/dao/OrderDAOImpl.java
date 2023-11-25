package com.colphacy.dao;

import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;
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

    private String getPaginatedOrdersByCriteria(OrderSearchCriteria criteria) {
// Use a CTE to filter the imports by keyword
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
}
