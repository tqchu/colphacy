package com.colphacy.dao;

import com.colphacy.dto.statistics.ImportRevenueStatisticsPointDTO;
import com.colphacy.dto.statistics.SoldProductDTO;
import com.colphacy.dto.statistics.SoldProductsRevenueDTO;
import com.colphacy.model.OrderStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StatisticsDAOImpl implements StatisticsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public int getNumberOfInStockProducts(Integer branchId) {
        boolean hasBranchCondition = branchId != null;
        String sql =
                """
                        WITH imported_quantity AS (SELECT pu.product_id,
                                                          pu.unit_id,
                                                          i.branch_id,
                                                          id.expiration_date,
                                                          sum(id.quantity) AS imported_quantity
                                                   FROM import i
                                                            JOIN import_detail id ON i.id = id.import_id
                                                            JOIN product_unit pu ON pu.unit_id = id.unit_id AND pu.product_id = id.product_id
                                                            JOIN branch b ON b.id = i.branch_id
                                                            """;
        if (hasBranchCondition) {
            sql += " AND b.id = :branchId ";
        }
        sql += """
                                                           GROUP BY pu.unit_id, i.branch_id, pu.product_id, id.expiration_date),
                sold_quantity AS (SELECT oi.product_id,
                                         oi.unit_id,
                                         o.branch_id,
                                         oi.expiration_date,
                                         sum(oi.quantity) AS ordered_quantity
                                  FROM orders o
                                           JOIN order_item oi ON o.id = oi.order_id
                                           JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                                           JOIN branch b ON b.id = o.branch_id
                                  WHERE o.status <> 'CANCELLED'
                                  """;
        if (hasBranchCondition) {
            sql += " AND b.id = :branchId ";
        }
        sql +=
                """
                        GROUP BY oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date)
                        SELECT COUNT(DISTINCT io.product_id)
                        FROM imported_quantity io
                                 LEFT JOIN sold_quantity oo
                                           ON io.product_id = oo.product_id AND io.branch_id = oo.branch_id AND io.unit_id = oo.unit_id AND
                                              io.expiration_date = oo.expiration_date
                        WHERE io.expiration_date > (CURRENT_DATE + INTERVAL '3 months')
                          AND (io.imported_quantity - COALESCE(oo.ordered_quantity, 0)) > 0;
                        """;
        Query query = entityManager.createNativeQuery(sql);
        if (hasBranchCondition) {
            query.setParameter("branchId", branchId);
        }
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public int getNumberOfNearDatedProducts(Integer branchId) {
        boolean hasBranchCondition = branchId != null;
        String sql =
                """
                        WITH imported_quantity AS (SELECT pu.product_id,
                                                          pu.unit_id,
                                                          i.branch_id,
                                                          id.expiration_date,
                                                          sum(id.quantity) AS imported_quantity
                                                   FROM import i
                                                            JOIN import_detail id ON i.id = id.import_id
                                                            JOIN product_unit pu ON pu.unit_id = id.unit_id AND pu.product_id = id.product_id
                                                            JOIN branch b ON b.id = i.branch_id
                                                            """;
        if (hasBranchCondition) {
            sql += " AND b.id = :branchId ";
        }
        sql += """
                                                           GROUP BY pu.unit_id, i.branch_id, pu.product_id, id.expiration_date),
                sold_quantity AS (SELECT oi.product_id,
                                         oi.unit_id,
                                         o.branch_id,
                                         oi.expiration_date,
                                         sum(oi.quantity) AS ordered_quantity
                                  FROM orders o
                                           JOIN order_item oi ON o.id = oi.order_id
                                           JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                                           JOIN branch b ON b.id = o.branch_id
                                  WHERE o.status <> 'CANCELLED'
                                  """;
        if (hasBranchCondition) {
            sql += " AND b.id = :branchId ";
        }
        sql +=
                """
                        GROUP BY oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date)
                        SELECT COUNT(DISTINCT (io.product_id, io.expiration_date))
                        FROM imported_quantity io
                                    LEFT JOIN sold_quantity oo
                                           ON io.product_id = oo.product_id AND io.branch_id = oo.branch_id AND io.unit_id = oo.unit_id AND
                                              io.expiration_date = oo.expiration_date
                        WHERE io.expiration_date < (CURRENT_DATE + INTERVAL '3 months')
                          AND (io.imported_quantity - COALESCE(oo.ordered_quantity, 0)) > 0
                        """;
        Query query = entityManager.createNativeQuery(sql);
        if (hasBranchCondition) {
            query.setParameter("branchId", branchId);
        }
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<SoldProductDTO> getSoldProducts(Integer branchId, Integer month, Integer year, String timeZone) {
        String sql =
                """
                        WITH
                             sold_quantity AS (SELECT oi.product_id,
                                                      oi.unit_id,
                                                      o.branch_id,
                                                      oi.expiration_date,
                                                      sum(oi.quantity) AS ordered_quantity
                                               FROM orders o
                                                        JOIN order_item oi ON o.id = oi.order_id
                                                        JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                                                        JOIN branch b ON b.id = o.branch_id
                                               WHERE o.status <> 'CANCELLED'
                                                    AND EXTRACT(YEAR FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                                               """;
        if (month != null) {
            sql += " AND EXTRACT(MONTH FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :month ";
        }
        if (branchId != null) {
            sql += " AND b.id = :branchId ";
        }
        sql +=
                """
                        GROUP BY oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date)
                        SELECT id, sold, name, image
                                FROM (
                                         SELECT DISTINCT ON (oo.product_id)
                                             oo.product_id as id,
                                             ordered_quantity as sold,
                                             p.name as name,
                                             pi.url as image
                                         FROM sold_quantity oo
                                                  JOIN product p ON p.id = oo.product_id
                                                  JOIN product_image pi ON pi.product_id = p.id
                                         ORDER BY oo.product_id, pi.url
                                     ) sub
                                ORDER BY sold DESC
                                LIMIT 5;
                        """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("year", year);
        query.setParameter("timeZone", timeZone);
        if (month != null)
            query.setParameter("month", month);
        if (branchId != null)
            query.setParameter("branchId", branchId);
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(SoldProductDTO.class))
                .getResultList();
    }

    @Override
    public SoldProductsRevenueDTO getSoldProductsAndRevenue(Integer branchId, Integer month, Integer year, String timeZoneStr) {
        String sql =
                """
                        WITH sold_quantity AS (SELECT oi.product_id,
                                                      oi.unit_id,
                                                      o.branch_id,
                                                      oi.expiration_date,
                                                      sum(oi.quantity) AS ordered_quantity,
                                                      sum(oi.quantity*oi.price) as total_price
                                               FROM orders o
                                                        JOIN order_item oi ON o.id = oi.order_id
                                                        JOIN product_unit pu ON pu.unit_id = oi.unit_id AND pu.product_id = oi.product_id
                                                        JOIN branch b ON b.id = o.branch_id
                                               WHERE o.status <> 'CANCELLED'
                                                 AND EXTRACT(YEAR FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                        """;
        if (month != null) {
            sql += " AND EXTRACT(MONTH FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :month ";
        }
        if (branchId != null) {
            sql += "  AND b.id = :branchId ";
        }
        sql +=
                """
                                               GROUP BY oi.product_id, o.branch_id, oi.unit_id, oi.expiration_date)
                        SELECT COUNT(DISTINCT oo.product_id) as total_sold_products,COALESCE(SUM(total_price), 0) as revenue
                        FROM sold_quantity oo
                        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("year", year);
        query.setParameter("timeZone", timeZoneStr);
        if (month != null)
            query.setParameter("month", month);
        if (branchId != null)
            query.setParameter("branchId", branchId);
        return (SoldProductsRevenueDTO) query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(SoldProductsRevenueDTO.class))
                .getSingleResult();

    }

    @Override
    public Map<OrderStatus, Integer> getOrderStatistics(Integer branchId, Integer month, Integer year, String timeZoneStr) {
        String sql =
                """
                        SELECT o.status, COUNT(DISTINCT  o.id)
                        FROM orders o
                        WHERE EXTRACT(YEAR
                                             FROM
                                             (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                        """;
        if (branchId != null) {
            sql += " AND o.branch_id = :branchId ";
        }
        if (month != null) {
            sql += " AND EXTRACT(MONTH FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :month ";
        }
        sql += """
                        GROUP BY o.status
                """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("year", year);
        query.setParameter("timeZone", timeZoneStr);
        if (month != null)
            query.setParameter("month", month);
        if (branchId != null)
            query.setParameter("branchId", branchId);

        List<Object[]> results = query.getResultList();
        Map<OrderStatus, Integer> statistics = new HashMap<>();
        for (Object[] result : results) {
            statistics.put(OrderStatus.valueOf((String) result[0]), ((Number) result[1]).intValue());
        }
        return statistics;


    }

    @Override
    public List<ImportRevenueStatisticsPointDTO> getPnlPoints(Integer branchId, Integer month, Integer year, String timeZoneStr) {
        Query query;
        if (month != null) {
            query = getQueryStatisticsForMonth(branchId, month, year, timeZoneStr);
        } else {
            query = getQueryStatisticsForYear(branchId, year, timeZoneStr);
        }
        query.setParameter("year", year);
        query.setParameter("timeZone", timeZoneStr);

        if (branchId != null)
            query.setParameter("branchId", branchId);
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ImportRevenueStatisticsPointDTO.class))
                .getResultList();
    }

    private Query getQueryStatisticsForYear(Integer branchId, Integer year, String timeZoneStr) {
        String sql =
                """                
                        WITH import_amount AS (
                        SELECT
                            EXTRACT(MONTH FROM (import_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) AS month,
                            sum(id.quantity * id.import_price) AS amount
                        FROM import i
                                 JOIN import_detail id ON i.id = id.import_id
                        WHERE EXTRACT(YEAR FROM (import_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                          """;
        if (branchId != null) {
            sql += " AND i.branch_id = :branchId ";
        }
        sql += """
                    GROUP BY month
                ),
                     months AS (
                         SELECT generate_series(1, :monthInYears) as month
                     ),
                     revenue AS (
                         SELECT
                             EXTRACT(MONTH FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) AS month,
                             sum(oi.quantity * oi.price) AS amount
                         FROM orders o
                                  JOIN order_item oi ON o.id = oi.order_id
                               """;
        if (branchId != null) {
            sql += " AND o.branch_id = :branchId";
        }
        sql +=
                """
                                 WHERE o.status <> 'CANCELLED'
                                   AND EXTRACT(YEAR FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                                 GROUP BY month
                             )
                        SELECT
                            COALESCE(ia.amount, 0) AS import_amount,
                            COALESCE(r.amount, 0) AS revenue
                        FROM months m
                                 LEFT JOIN import_amount ia ON m.month = ia.month
                                 LEFT JOIN revenue r ON m.month = r.month
                                 ORDER BY m.month;
                            """;
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        int monthInYears = 12;
        if (year == now.getYear()) {
            monthInYears = now.getMonthValue();
        }
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("monthInYears", monthInYears);
        return query;
    }

    private Query getQueryStatisticsForMonth(Integer branchId, Integer month, Integer year, String timeZoneStr) {
        String sql =
                """                
                        WITH import_amount AS (
                        SELECT
                            EXTRACT(DAY FROM (import_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) AS day,
                            sum(id.quantity * id.import_price) AS amount
                        FROM import i
                                 JOIN import_detail id ON i.id = id.import_id
                        WHERE EXTRACT(YEAR FROM (import_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                          AND EXTRACT(MONTH FROM (import_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :month
                          """;
        if (branchId != null) {
            sql += " AND i.branch_id = :branchId ";
        }
        sql += """
                    GROUP BY day
                ),
                     days AS (
                         SELECT generate_series(1, :daysInMonth) as day
                     ),
                     revenue AS (
                         SELECT
                             EXTRACT(DAY FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) AS day,
                             sum(oi.quantity * oi.price) AS amount
                         FROM orders o
                                  JOIN order_item oi ON o.id = oi.order_id
                               """;
        if (branchId != null) {
            sql += " AND o.branch_id = :branchId";
        }
        sql +=
                """
                                 WHERE o.status <> 'CANCELLED'
                                   AND EXTRACT(YEAR FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :year
                                   AND EXTRACT(MONTH FROM (order_time AT TIME ZONE 'UTC') AT TIME ZONE :timeZone) = :month
                                 GROUP BY day
                             )
                        SELECT
                            COALESCE(ia.amount, 0) AS import_amount,
                            COALESCE(r.amount, 0) AS revenue
                        FROM days d
                                 LEFT JOIN import_amount ia ON d.day = ia.day
                                 LEFT JOIN revenue r ON d.day = r.day
                                 ORDER BY d.day;
                            """;
        ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of(timeZoneStr));
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        int daysInMonth;
        if (month == now.getMonthValue() && year == now.getYear()) {
            daysInMonth = now.getDayOfMonth();
        } else {
            daysInMonth = zonedDateTime.toLocalDate().lengthOfMonth();
        }
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("daysInMonth", daysInMonth);
        query.setParameter("month", month);
        return query;
    }
}