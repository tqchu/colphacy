package com.colphacy.repository;

import com.colphacy.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value =
            """
                    SELECT CASE
                           WHEN EXISTS (
                                    SELECT 1
                                    FROM orders o
                                             JOIN order_item oi ON o.id = oi.order_id
                                    WHERE o.customer_id = :customerId
                                      AND oi.product_id = :productId
                                      AND o.status = 'DELIVERED'
                                )
                                AND NOT EXISTS (
                                    SELECT 1
                                    FROM reviews r
                                    WHERE r.customer_id = :customerId
                                      AND r.product_id = :productId
                                ) THEN true
                           ELSE false END
                                      """,
            nativeQuery = true)
    boolean canCustomerReviewProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);
}
