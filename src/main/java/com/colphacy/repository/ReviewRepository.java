package com.colphacy.repository;

import com.colphacy.dto.review.ReviewAdminListViewDTO;
import com.colphacy.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query(value = "select * from reviews r where r.product_id= :productId and r.parent_review_id is null order by r.created_time", nativeQuery = true)
    Page<Review> findByProductIdAndAndParentReviewIsNull(Long productId, Pageable pageable);

    @Query(value = "select * from reviews r where r.parent_review_id = :parentReviewId order by r.created_time", nativeQuery = true)
    Review findByParentReviewId(Long parentReviewId);

    @Query("""
        SELECT r.id as id,
               r.product.id as product_id,
               r.product.name as product_name,
               pi.url as product_image,
               c.fullName as customer_name,
               r.customer.id as customer_id,
               r.rating as rating,
               r.content as content,
               r.createdTime as created_time,
               child.id as reply_review_id,
               child.content as reply_review_content,
               child.createdTime as reply_review_created_time,
               e.id as employee_id,
               e.fullName
        FROM reviews r
                 LEFT JOIN ProductImage pi ON r.product.id = pi.product.id
                 LEFT JOIN customer c ON r.customer.id = c.id
                 LEFT JOIN reviews child ON r.id = child.parentReview.id
                 LEFT JOIN employee e ON child.employee.id = e.id
        WHERE (unaccent(lower(r.product.name)) LIKE unaccent(lower(concat('%', :keyword, '%')))
               OR unaccent(lower(c.fullName)) LIKE unaccent(lower(concat('%', :keyword, '%'))) 
               OR unaccent(lower(r.content)) LIKE unaccent(lower(concat('%', :keyword, '%'))))
            AND r.parentReview is null
        """)
    Page<Object[]> findAllReviewsWithKeyword(String keyword, Pageable pageable);

    @Query("""
        SELECT r.id as id,
               r.product.id as product_id,
               r.product.name as product_name,
               pi.url as product_image,
               c.fullName as customer_name,
               r.customer.id as customer_id,
               r.rating as rating,
               r.content as content,
               r.createdTime as created_time,
               child.id as reply_review_id,
               child.content as reply_review_content,
               child.createdTime as reply_review_created_time,
               e.id as employee_id,
               e.fullName
        FROM reviews r
                 LEFT JOIN ProductImage pi ON r.product.id = pi.product.id
                 LEFT JOIN customer c ON r.customer.id = c.id
                 LEFT JOIN reviews child ON r.id = child.parentReview.id
                 LEFT JOIN employee e ON child.employee.id = e.id
        WHERE r.parentReview is null
        """)
    Page<Object[]> findAllReviews(Pageable pageable);
}
