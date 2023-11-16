package com.colphacy.dao;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductSearchCriteria;
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
        String sql = getPaginatedProductsAdminByKeywordAndCategoryIdQuery(keyword, categoryId, paginationRequest.getSortBy(), paginationRequest.getOrder());

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

    private static String getPaginatedProductsAdminByKeywordAndCategoryIdQuery(String keyword, Integer categoryId, String sortBy, String order) {
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

    private static String getCountProductsAdminByKeywordAndCategoryIdQuery(String keyword, Integer categoryId) {
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
        String sql = getCountProductsAdminByKeywordAndCategoryIdQuery(keyword, categoryId);

        Query query = entityManager.createNativeQuery(sql);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (keyword != null) {
            query.setParameter("keyword", keyword);
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public List<ProductCustomerListViewDTO> getPaginatedProductsCustomer(ProductSearchCriteria productSearchCriteria) {
        String sql = getPaginatedProductsCustomerByCriteria(productSearchCriteria);

        Query query = entityManager.createNativeQuery(sql);


        boolean hasCategoryCondition = productSearchCriteria.getCategoryIds() != null;
        boolean hasKeywordCondition = productSearchCriteria.getKeyword() != null;
        boolean hasMinPriceCondition = productSearchCriteria.getMinPrice() != null;
        boolean hasMaxPriceCondition = productSearchCriteria.getMaxPrice() != null;

        if (hasCategoryCondition) {
            query.setParameter("categoryIds", productSearchCriteria.getCategoryIds());
        }
        if (hasKeywordCondition) {
            query.setParameter("keyword", productSearchCriteria.getKeyword());
        }
        if (hasMinPriceCondition) {
            query.setParameter("minPrice", productSearchCriteria.getMinPrice());
        }
        if (hasMaxPriceCondition) {
            query.setParameter("maxPrice", productSearchCriteria.getMaxPrice());
        }

        query.setParameter("limit", productSearchCriteria.getLimit());
        query.setParameter("offset", productSearchCriteria.getOffset());

        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductCustomerListViewDTO.class))
                .getResultList();
    }

    private String getPaginatedProductsCustomerByCriteria(ProductSearchCriteria productSearchCriteria) {
        String sql = """
                SELECT p.id               as id,
                       p.name             as name,
                       min(u.name)        as unit_name,
                       min(pu.sale_price) as sale_price,
                       min(pi.url) as image
                FROM product p
                         JOIN product_unit pu
                              ON pu.product_id = p.id
                         JOIN category c
                              ON c.id = p.category_id
                         JOIN (SELECT product_id, MAX(sale_price) as sale_price
                               FROM product_unit
                               GROUP BY product_id) main_pu
                              ON p.id = main_pu.product_id AND pu.sale_price = main_pu.sale_price
                         JOIN unit u
                              ON u.id = pu.unit_id
                         LEFT JOIN product_image pi
                              ON pi.product_id = p.id
                """;

        boolean hasCategoryCondition = productSearchCriteria.getCategoryIds() != null;
        boolean hasKeywordCondition = productSearchCriteria.getKeyword() != null;
        boolean hasMinPriceCondition = productSearchCriteria.getMinPrice() != null;
        boolean hasMaxPriceCondition = productSearchCriteria.getMaxPrice() != null;
        if (hasCategoryCondition || hasKeywordCondition || hasMinPriceCondition || hasMaxPriceCondition) {
            sql += "WHERE ";
            if (hasCategoryCondition) {
                sql += "p.category_id IN (:categoryIds)";
                if (hasMinPriceCondition || hasMaxPriceCondition || hasKeywordCondition) {
                    sql += " AND ";
                }
            }
            if (hasKeywordCondition) {
                sql += " unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%')) or unaccent(lower(p.short_description)) LIKE unaccent(lower('%' || :keyword || '%')) or unaccent(lower(p.full_description)) LIKE unaccent(lower('%' || :keyword || '%')) ";
                if (hasMinPriceCondition || hasMaxPriceCondition) {
                    sql += " AND ";
                }
            }
            if (hasMinPriceCondition) {
                sql += " pu.sale_price>= :minPrice ";
                if (hasMaxPriceCondition) {
                    sql += " AND ";
                }
            }
            if (hasMaxPriceCondition) {
                sql += " pu.sale_price <= :maxPrice ";
            }
        }

        sql += "GROUP BY p.id, p.name ORDER BY " + productSearchCriteria.getSortBy() + " " + productSearchCriteria.getOrder() + " LIMIT :limit OFFSET :offset";
        return sql;
    }

    public Long getTotalProductsCustomer(ProductSearchCriteria criteria) {
        String sql = getCountProductsCustomerByKeywordAndCategoryIdQuery(criteria);

        Query query = entityManager.createNativeQuery(sql);
        boolean hasCategoryCondition = criteria.getCategoryIds() != null;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasMinPriceCondition = criteria.getMinPrice() != null;
        boolean hasMaxPriceCondition = criteria.getMaxPrice() != null;
        if (hasCategoryCondition) {
            query.setParameter("categoryIds", criteria.getCategoryIds());
        }
        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (hasMinPriceCondition) {
            query.setParameter("minPrice", criteria.getMinPrice());
        }
        if (hasMaxPriceCondition) {
            query.setParameter("maxPrice", criteria.getMaxPrice());
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private String getCountProductsCustomerByKeywordAndCategoryIdQuery(ProductSearchCriteria criteria) {
        String sql = """
                SELECT COUNT (list.id) FROM 
                (SELECT  p.id as id
                FROM product p
                         JOIN product_unit pu
                              ON pu.product_id = p.id
                         JOIN category c
                              ON c.id = p.category_id
                         JOIN (SELECT product_id, MAX(sale_price) as sale_price
                               FROM product_unit
                               GROUP BY product_id) main_pu
                              ON p.id = main_pu.product_id AND pu.sale_price = main_pu.sale_price
                         JOIN unit u
                              ON u.id = pu.unit_id
                         LEFT JOIN product_image pi
                              ON pi.product_id = p.id
                """;

        boolean hasCategoryCondition = criteria.getCategoryIds() != null;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasMinPriceCondition = criteria.getMinPrice() != null;
        boolean hasMaxPriceCondition = criteria.getMaxPrice() != null;

        if (hasCategoryCondition || hasKeywordCondition || hasMinPriceCondition || hasMaxPriceCondition) {
            sql += "WHERE ";
            if (hasCategoryCondition) {
                sql += "p.category_id IN (:categoryIds)";
                if (hasMinPriceCondition || hasMaxPriceCondition || hasKeywordCondition) {
                    sql += " AND ";
                }
            }
            if (hasKeywordCondition) {
                sql += " unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%')) or unaccent(lower(p.short_description)) LIKE unaccent(lower('%' || :keyword || '%')) or unaccent(lower(p.full_description)) LIKE unaccent(lower('%' || :keyword || '%')) ";
                if (hasMinPriceCondition || hasMaxPriceCondition) {
                    sql += " AND ";
                }
            }
            if (hasMinPriceCondition) {
                sql += " pu.sale_price>= :minPrice ";
                if (hasMaxPriceCondition) {
                    sql += " AND ";
                }
            }
            if (hasMaxPriceCondition) {
                sql += " pu.sale_price <= :maxPrice ";
            }
        }

        sql += "GROUP BY p.id, p.name) as list";
        return sql;
    }
}

