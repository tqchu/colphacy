package com.colphacy.dao;

import com.colphacy.dto.stock.StockListViewDTO;
import com.colphacy.dto.stock.StockSearchCriteria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class StockDAOImpl implements StockDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StockListViewDTO> getStockView(StockSearchCriteria criteria) {
        // Get the native SQL query
        String sql = getStockViewQuery(criteria);

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        if (criteria.getBranchId() != null) {
            query.setParameter("branchId", criteria.getBranchId());
        }

        // Set the parameters based on the criteria
        boolean hasKeywordCondition = criteria.getKeyword() != null;

        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }

        if (criteria.getBranchId() != null) {
            query.setParameter("branchId", criteria.getBranchId());
        }

        query.setParameter("limit", criteria.getLimit());
        query.setParameter("offset", criteria.getOffset());

        // Execute the query and return the result list, with each result transformed into an OrderListViewDTO object
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(StockListViewDTO.class))
                .getResultList();
    }

    @Override
    public Long getTotalStock(StockSearchCriteria criteria) {
        String sql = getTotalStockQuery(criteria);

        Query query = entityManager.createNativeQuery(sql);

        if (criteria.getKeyword() != null) {
            query.setParameter("keyword", criteria.getKeyword());
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    public String getTotalStockQuery(StockSearchCriteria criteria) {
        String sql =
                """
                        SELECT COUNT(*)
                        FROM product p
                                """;
        if (criteria.getKeyword() != null) {
            sql += " WHERE unaccent (lower(p.name)) LIKE unaccent (lower('%' || :keyword || '%')) ";
        }
        return sql;
    }

    private String getStockViewQuery(StockSearchCriteria criteria) {
        String sql =
                """
                        WITH imported_quantity AS (SELECT pu.product_id,
                                                          pu.unit_id,
                                                          id.expiration_date,
                                                          SUM(COALESCE(id.quantity, 0)) AS imported_quantity
                                                   FROM import i
                                                            JOIN import_detail id ON i.id = id.import_id
                                                            RIGHT JOIN product_unit pu ON pu.unit_id = id.unit_id AND pu.product_id = id.product_id
                                                            """;
        if (criteria.getKeyword() != null) {
            sql += """
                    JOIN product p ON p.id = id.product_id
                                    AND unaccent (lower(p.name)) LIKE unaccent (lower('%' || :keyword || '%'))
                    """;
        }
        if (criteria.getBranchId() != null) {
            sql += " WHERE i.branch_id = :branchId ";
        }
        sql +=
                """
                        GROUP BY pu.unit_id, pu.product_id, id.expiration_date),
                        sold_quantity AS (SELECT oi.product_id,
                                                 oi.unit_id,
                                                 oi.expiration_date,
                                                 SUM(COALESCE(oi.quantity, 0)) AS ordered_quantity
                                          FROM orders o
                                                   JOIN order_item oi ON o.id = oi.order_id
                                                   RIGHT JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id 
                                                   """;
        if (criteria.getKeyword() != null) {
            sql +=
                    """
                               JOIN product p ON p.id = oi.product_id
                               AND unaccent (lower(p.name)) LIKE unaccent (lower('%' || :keyword || '%'))
                               WHERE 
                                    o.status<> 'CANCELLED'
                               
                            """;
        }
        if (criteria.getBranchId() != null) {
            sql += " AND o.branch_id = :branchId ";
        }
        sql +=
                """
                        GROUP BY oi.product_id, oi.unit_id, oi.expiration_date)
                        SELECT io.product_id as product_id,
                                io.unit_id as unit_id,
                        p.name as product_name,
                                u.name as unit_name,
                        io.expiration_date as expiration_date,
                                sum(io.imported_quantity - COALESCE(oo.ordered_quantity, 0)) AS quantity
                        FROM((SELECT io.product_id as product_id,
                                sum(io.imported_quantity - COALESCE(oo.ordered_quantity,0)) as total_quantity
                                FROM imported_quantity io
                                LEFT JOIN sold_quantity oo
                                ON io.product_id = oo.product_id AND io.unit_id = oo.unit_id AND
                                io.expiration_date = oo.expiration_date
                                JOIN product p
                                ON p.id = io.product_id
                                JOIN unit u
                                ON u.id = io.unit_id
                                GROUP BY io.product_id
                                """;
        sql += "ORDER BY " + (criteria.getSortBy() != null ? " total_quantity " : " io.product_id ");
        sql += (criteria.getOrder() == null ? " DESC " : criteria.getOrder());
        sql +=
                """
                         LIMIT :limit OFFSET :offset)) as filtered_products
                        JOIN imported_quantity io
                        ON filtered_products.product_id = io.product_id
                        LEFT JOIN sold_quantity oo
                        ON io.product_id = oo.product_id AND io.unit_id = oo.unit_id AND
                        io.expiration_date = oo.expiration_date
                        JOIN product p
                        ON p.id = io.product_id
                        JOIN unit u
                        ON u.id = io.unit_id
                        GROUP BY io.product_id, io.unit_id, io.expiration_date, p.name, u.name
                        """;
        sql += ", total_quantity ORDER BY " + (criteria.getSortBy() != null ? " total_quantity " : " io.product_id ");
        sql += (criteria.getOrder() == null ? " DESC " : criteria.getOrder());
        return sql;

    }
}
