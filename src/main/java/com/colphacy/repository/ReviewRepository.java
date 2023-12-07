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

    @Query(value = """
            SELECT r.id as id,
                   p.id as product_id,
                   p.name as product_name,
                   (SELECT pi.url
                    FROM product_image pi
                    WHERE pi.product_id = r.product_id
                    ORDER BY pi.id
                    LIMIT 1) as product_image,
                   c.full_name as customer_name,
                   r.customer_id as customer_id,
                   r.rating as rating,
                   r.content as content,
                   r.created_time as created_time,
                   child.id as reply_review_id,
                   child.content as reply_review_content,
                   child.created_time as reply_review_created_time,
                   e.id as employee_id,
                   e.full_name as employee_name
            FROM reviews r
                     JOIN customer c ON r.customer_id = c.id
                     LEFT JOIN reviews child ON r.id = child.parent_review_id
                     LEFT JOIN employee e ON child.employee_id = e.id
                     JOIN product p ON r.product_id = p.id
            WHERE (unaccent(lower(r.content)) LIKE unaccent(lower('%' || :keyword || '%'))
            OR unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
            OR unaccent(lower(c.full_name)) LIKE unaccent(lower('%' || :keyword || '%')))
              AND r.parent_review_id is null
            """, nativeQuery = true)
    Page<Object[]> findAllReviewsWithKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
                SELECT r.id as id,
                       p.id as product_id,
                       p.name as product_name,
                       (SELECT pi.url
                        FROM product_image pi
                        WHERE pi.product_id = r.product_id
                        ORDER BY pi.id
                        LIMIT 1) as product_image,
                       c.full_name as customer_name,
                       r.customer_id as customer_id,
                       r.rating as rating,
                       r.content as content,
                       r.created_time as created_time,
                       child.id as reply_review_id,
                       child.content as reply_review_content,
                       child.created_time as reply_review_created_time,
                       e.id as employee_id,
                       e.full_name as employee_name
                FROM reviews r
                         JOIN customer c ON r.customer_id = c.id
                         LEFT JOIN reviews child ON r.id = child.parent_review_id
                         LEFT JOIN employee e ON child.employee_id = e.id
                         JOIN product p ON r.product_id = p.id
                WHERE r.parent_review_id IS NULL
            """, nativeQuery = true)
    Page<Object[]> findAllReviews(Pageable pageable);
}
