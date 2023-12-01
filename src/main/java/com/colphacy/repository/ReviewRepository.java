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
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO reviews (rating, content, product_id, customer_id) VALUES (:rating, :content, :productId, :customerId)", nativeQuery = true)
    void create(int rating, String content, Long productId, Long customerId);

    @Query(value =
            "SELECT CASE\n" +
                    "           WHEN EXISTS (\n" +
                    "                    SELECT 1\n" +
                    "                    FROM orders o\n" +
                    "                             JOIN order_item oi ON o.id = oi.order_id\n" +
                    "                    WHERE o.customer_id = :customerId\n" +
                    "                      AND oi.product_id = :productId\n" +
                    "                      AND o.status = 'DELIVERED'\n" +
                    "                )\n" +
                    "                AND NOT EXISTS (\n" +
                    "                    SELECT 1\n" +
                    "                    FROM reviews r\n" +
                    "                    WHERE r.customer_id = :customerId\n" +
                    "                      AND r.product_id = :productId\n" +
                    "                ) THEN true\n" +
                    "           ELSE false END",
            nativeQuery = true)
    boolean canCustomerReviewProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);
}
