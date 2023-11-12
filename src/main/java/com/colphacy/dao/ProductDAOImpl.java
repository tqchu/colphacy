package com.colphacy.dao;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.types.PaginationRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ProductDAOImpl implements ProductDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductAdminListViewDTO> getPaginatedProductsAdmin(String keyword, Integer categoryId, PaginationRequest paginationRequest) {
        String sql = getPaginatedProductsByKeywordAndCategoryIdQuery(keyword, categoryId, paginationRequest.getSortBy(), paginationRequest.getOrder());

        Query query = entityManager.createNativeQuery(sql);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (keyword != null) {
            query.setParameter("keyword", keyword);
        }

        query.setParameter("limit", paginationRequest.getLimit());
        query.setParameter("offset", paginationRequest.getOffset());

        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductAdminListViewDTO.class))
                .getResultList();
    }

    private static String getPaginatedProductsByKeywordAndCategoryIdQuery(String keyword, Integer categoryId, String sortBy, String order) {
        String sql = "SELECT p.id as id, p.name as name, c.name as category_name, MAX(pu.sale_price) AS sale_price, 50000 as import_price " +
                "FROM product p JOIN product_unit pu ON p.id = pu.product_id JOIN category c ON c.id = p.category_id ";

        if (categoryId != null || keyword != null) {
            sql += "WHERE ";
            if (categoryId != null) {
                sql += "p.category_id = :categoryId ";
                if (keyword != null) {
                    sql += "AND ";
                }
            }
            if (keyword != null) {
                sql += "unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%')) ";
            }
        }

        sql += "GROUP BY p.id, c.name ORDER BY " + sortBy + " " + order + (!sortBy.equals("id") ? ", id ASC " : "") + " LIMIT :limit OFFSET :offset";
        return sql;
    }

    private static String getCountProductsByKeywordAndCategoryIdQuery(String keyword, Integer categoryId) {
        String sql = "SELECT COUNT(*) " +
                "FROM (SELECT p.id " +
                "FROM product p JOIN product_unit pu ON p.id = pu.product_id JOIN category c ON c.id = p.category_id ";

        if (categoryId != null || keyword != null) {
            sql += "WHERE ";
            if (categoryId != null) {
                sql += "p.category_id = :categoryId ";
                if (keyword != null) {
                    sql += "AND ";
                }
            }
            if (keyword != null) {
                sql += "unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%')) ";
            }
        }

        sql += "GROUP BY p.id, c.name) as count_query";
        return sql;
    }

    public Long getTotalProductsAdmin(String keyword, Integer categoryId) {
        String sql = getCountProductsByKeywordAndCategoryIdQuery(keyword, categoryId);

        Query query = entityManager.createNativeQuery(sql);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (keyword != null) {
            query.setParameter("keyword", keyword);
        }

        return ((Number) query.getSingleResult()).longValue();
    }


}

