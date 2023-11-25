package com.colphacy.repository;

import com.colphacy.dto.orderItem.OrderItemCreateDTO;
import com.colphacy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "WITH quantity_sum AS (" +
            "SELECT t.product_id, " +
            "t.unit_id, " +
            "t.branch_id, " +
            "t.quantity, " +
            "SUM(t.quantity) OVER (PARTITION BY t.product_id, t.unit_id, t.branch_id ORDER BY t.branch_id ASC, t.expiration_date ASC ROWS UNBOUNDED PRECEDING) as running_total, " +
            "t.expiration_date " +
            "FROM branches_view t " +
            "JOIN unnest(cast(:sets as array<record(product_id bigint, unit_id bigint)>)) s(product_id, unit_id) " +
            "ON t.product_id = s.product_id AND t.unit_id = s.unit_id" +
            "), max_running_total AS (" +
            "SELECT product_id, " +
            "unit_id, " +
            "branch_id, " +
            "quantity, " +
            "MAX(running_total) OVER (PARTITION BY product_id, unit_id, branch_id) as max_running_total " +
            "FROM quantity_sum) " +
            "SELECT qs.product_id, " +
            "qs.unit_id, " +
            "qs.branch_id, " +
            "CASE WHEN qs.running_total < s.quantity THEN qs.quantity " +
            "ELSE s.quantity - (qs.running_total - s.quantity) END as quantity, " +
            "qs.expiration_date " +
            "FROM quantity_sum qs " +
            "JOIN max_running_total mrt " +
            "ON mrt.branch_id = qs.branch_id " +
            "AND mrt.product_id = qs.product_id " +
            "AND mrt.unit_id = qs.unit_id " +
            "AND mrt.quantity = qs.quantity " +
            "JOIN unnest(cast(:sets as array<record(product_id bigint, unit_id bigint, quantity int, price double precision)>)) s(product_id, unit_id, quantity, price) " +
            "ON mrt.product_id = s.product_id AND mrt.unit_id = s.unit_id " +
            "WHERE qs.branch_id = (" +
            "SELECT qs.branch_id " +
            "FROM quantity_sum qs " +
            "JOIN max_running_total mrt " +
            "ON mrt.branch_id = qs.branch_id " +
            "AND mrt.product_id = qs.product_id " +
            "AND mrt.unit_id = qs.unit_id " +
            "AND mrt.quantity = qs.quantity " +
            "JOIN public.branch b ON qs.branch_id = b.id " +
            "WHERE mrt.max_running_total >= s.quantity " +
            "AND ((s.quantity - (qs.running_total - s.quantity)) > 0) " +
            "GROUP BY qs.branch_id, b.latitude, b.longitude " +
            "HAVING COUNT(distinct (qs.product_id, qs.unit_id, qs.branch_id)) = :size " +
            "ORDER BY (6371 * acos(cos(radians(21.008640)) * cos(radians(b.latitude)) " +
            "* cos(radians(b.longitude) - radians(105.840830)) + sin(radians(21.008640)) " +
            "* sin(radians(b.latitude)))) " +
            "LIMIT 1) " +
            "AND mrt.max_running_total >= s.quantity " +
            "AND ((s.quantity - (qs.running_total - s.quantity)) > 0)",
            nativeQuery = true)
    List<Object[]> findSuitableProduct(@Param("sets") List<OrderItemCreateDTO> sets, @Param("size") Integer size);
}
