package com.colphacy.dao;

import com.colphacy.dto.review.ReviewAdminListViewDTO;
import com.colphacy.types.PaginationRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ReviewDAOImpl implements ReviewDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReviewAdminListViewDTO> getAllReviewsForAdmin(String keyword, PaginationRequest paginationRequest) {
        String sql = getAllReviewsForAdminByKeywordQuery(keyword, paginationRequest.getSortBy(), paginationRequest.getOrder());

        Query query = entityManager.createNativeQuery(sql);

        if (keyword != null) {
            query.setParameter("keyword", keyword);
        }

        query.setParameter("limit", paginationRequest.getLimit());
        query.setParameter("offset", paginationRequest.getOffset());

        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ReviewAdminListViewDTO.class))
                .getResultList();
    }

    @Override
    public Long getTotalReviewsForAdmin(String keyword) {
        String sql = getCountReviewsForAdminByKeywordQuery(keyword);

        Query query = entityManager.createNativeQuery(sql);

        if (keyword != null) {
            query.setParameter("keyword", keyword);
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private String getCountReviewsForAdminByKeywordQuery(String keyword) {
        String sql = """
            SELECT COUNT(*) as count_query
            FROM (
                SELECT r.id
                FROM reviews r
                    JOIN customer c ON r.customer_id = c.id
                    LEFT JOIN reviews child ON r.id = child.parent_review_id
                    LEFT JOIN employee e ON child.employee_id = e.id
                    JOIN product p ON r.product_id = p.id
                WHERE r.parent_review_id IS NULL
            """;

        if (keyword != null) {
            sql += """
                AND (unaccent(lower(r.content)) LIKE unaccent(lower('%' || :keyword || '%'))
                    OR unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                    OR unaccent(lower(c.full_name)) LIKE unaccent(lower('%' || :keyword || '%')))
                """;
        }

        sql += ") as count_query";

        return sql;
    }

    private String getAllReviewsForAdminByKeywordQuery(String keyword, String sortBy, String order) {
        String sql = """
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
                """;

        if (keyword != null) {
            sql += " " +  """
                    AND(unaccent(lower(r.content)) LIKE unaccent(lower('%' || :keyword || '%'))
                    OR unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                    OR unaccent(lower(c.full_name)) LIKE unaccent(lower('%' || :keyword || '%')))
                            """;
        }
        sql += "ORDER BY " + sortBy + " " + order  + " LIMIT :limit OFFSET :offset";
        return sql;
    }
}
