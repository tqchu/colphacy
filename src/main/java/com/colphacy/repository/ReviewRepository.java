package com.colphacy.repository;

import com.colphacy.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
                                      AND oi.is_reviewed is false
                                )
                                THEN true
                           ELSE false END
                                      """,
            nativeQuery = true)
    boolean canCustomerReviewProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);

    @Query(value = "select * from reviews r where r.product_id= :productId and r.parent_review_id is null order by r.created_time", nativeQuery = true)
    Page<Review> findByProductIdAndAndParentReviewIsNull(Long productId, Pageable pageable);

    @Query(value = "select * from reviews r where r.parent_review_id = :parentReviewId order by r.created_time", nativeQuery = true)
    Review findByParentReviewId(Long parentReviewId);

    Optional<Review> findByIdAndParentReviewIsNull(Long id);
}
