package com.colphacy.dao;

import com.colphacy.dto.imports.ImportListViewDTO;
import com.colphacy.dto.imports.ImportSearchCriteria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ImportDAOImpl implements ImportDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<ImportListViewDTO> getPaginatedImports(ImportSearchCriteria criteria) {
        // Get the native SQL query
        String sql = getPaginatedImportsByCriteria(criteria);

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        // Set the parameters based on the criteria
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;

        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (hasStartDateCondition) {
            query.setParameter("startDate", criteria.getStartDate());
        }
        if (hasEndDateCondition) {
            query.setParameter("endDate", criteria.getEndDate());
        }
        if (hasBranchIdCondition) {
            query.setParameter("branchId", criteria.getBranchId());
        }
        query.setParameter("limit", criteria.getLimit());
        query.setParameter("offset", criteria.getOffset());

        // Execute the query and return the result list, with each result transformed into an ImportListViewDTO object
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(ImportListViewDTO.class))
                .getResultList();
    }

    @Override
    public Long getTotalImports(ImportSearchCriteria criteria) {
        // Get the SQL query to count the imports
        String sql = getCountImportsByCriteria(criteria);

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        // Set the parameters based on the criteria
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;

        if (hasKeywordCondition) {
            query.setParameter("keyword", criteria.getKeyword());
        }
        if (hasStartDateCondition) {
            query.setParameter("startDate", criteria.getStartDate());
        }
        if (hasEndDateCondition) {
            query.setParameter("endDate", criteria.getEndDate());
        }
        if (hasBranchIdCondition) {
            query.setParameter("branchId", criteria.getBranchId());
        }

        // Execute the query and return the result as a long value
        return ((Number) query.getSingleResult()).longValue();
    }


    private String getCountImportsByCriteria(ImportSearchCriteria criteria) {
        String sql = """
                SELECT COUNT (list.id) FROM 
                (SELECT i.id as id
                FROM import i
                         JOIN import_detail id ON i.id = id.import_id
                         JOIN employee e ON e.id = i.employee_id
                         JOIN product p ON p.id = id.product_id
                """;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;

        if (hasStartDateCondition || hasEndDateCondition || hasBranchIdCondition || hasKeywordCondition) {
            sql += " WHERE ";
            if (hasKeywordCondition) {
                sql += "unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))";
                if (hasStartDateCondition || hasEndDateCondition || hasBranchIdCondition) {
                    sql += " AND ";
                }
            }
            if (hasStartDateCondition) {
                sql += "import_time >= :startDate";
                if (hasEndDateCondition || hasBranchIdCondition) {
                    sql += " AND ";
                }
            }
            if (hasEndDateCondition) {
                sql += "import_time <= :endDate ";
                if (hasBranchIdCondition) {
                    sql += " AND ";
                }
            }
            if (hasBranchIdCondition) {
                sql += "i.branch_id = :branchId";
            }
        }

        // Add the grouping clause
        sql += " GROUP BY i.id, e.id) as list";
        return sql;
    }

    private String getPaginatedImportsByCriteria(ImportSearchCriteria criteria) {
        // Use a CTE to filter the imports by keyword
        String sql;
        boolean hasKeywordCondition = criteria.getKeyword() != null;
        if (hasKeywordCondition) {
            sql = """
                    WITH filtered_imports AS (
                        SELECT i.id
                        FROM import i
                                 JOIN import_detail id ON i.id = id.import_id
                                 JOIN product p ON p.id = id.product_id
                        WHERE unaccent(lower(p.name)) LIKE unaccent(lower('%' || :keyword || '%'))
                    )
                                
                    SELECT i.id                            as id,
                           i.invoice_number                as invoice_number,
                           i.import_time                   as import_time,
                           SUM(id.import_price * quantity) as total,
                           e.full_name                     as employee
                    FROM import i
                             JOIN import_detail id ON i.id = id.import_id
                             JOIN employee e ON e.id = i.employee_id
                    WHERE i.id IN (SELECT id FROM filtered_imports)
                    """;
        } else {
            sql = """
                    SELECT i.id                            as id,
                           i.invoice_number                as invoice_number,
                           i.import_time                   as import_time,
                           SUM(id.import_price * quantity) as total,
                           e.full_name                     as employee
                    FROM import i
                             JOIN import_detail id ON i.id = id.import_id
                             JOIN employee e ON e.id = i.employee_id
                    """;
        }

        // Add the date range and branch id conditions if they are present
        boolean hasStartDateCondition = criteria.getStartDate() != null;
        boolean hasEndDateCondition = criteria.getEndDate() != null;
        boolean hasBranchIdCondition = criteria.getBranchId() != null;

        if (hasStartDateCondition || hasEndDateCondition || hasBranchIdCondition) {
            sql += hasKeywordCondition ? " AND " : " WHERE ";
            if (hasStartDateCondition) {
                sql += "import_time >= :startDate";
                if (hasEndDateCondition || hasBranchIdCondition) {
                    sql += " AND ";
                }
            }
            if (hasEndDateCondition) {
                sql += " import_time <= :endDate ";
                if (hasBranchIdCondition) {
                    sql += " AND ";
                }
            }
            if (hasBranchIdCondition) {
                sql += "i.branch_id = :branchId";
            }
        }

        // Add the grouping and ordering clauses
        sql += " GROUP BY i.id, e.id ORDER BY " + criteria.getSortBy() + " " + criteria.getOrder() + " LIMIT :limit OFFSET :offset";
        return sql;
    }


}
