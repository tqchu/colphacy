package com.colphacy.repository;


import com.colphacy.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Transactional
    @Query(value =
            """
                    UPDATE order_item
                    SET is_reviewed = true
                    FROM orders
                    WHERE orders.id = order_item.order_id
                      AND orders.customer_id = :customerId
                      AND order_item.product_id = :productId
                      AND orders.status = 'DELIVERED'
                      AND order_item.is_reviewed is false
                                      """, nativeQuery = true)
    void updateIsReviewedToTrue(Long customerId, Long productId);
}
