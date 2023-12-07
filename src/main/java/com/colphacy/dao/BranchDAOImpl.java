package com.colphacy.dao;

import com.colphacy.dto.branch.FindNearestBranchCriteria;
import com.colphacy.dto.branch.FlattenedBranchDTO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Repository
public class BranchDAOImpl implements BranchDAO {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FlattenedBranchDTO> findNearestBranches(FindNearestBranchCriteria criteria) {
        // Get the native SQL query
        String sql = """
                SELECT id, street_address, ward, district, province
                FROM branch
                ORDER BY  acos(cos(radians(:latitude)) *cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude))+
                   sin(radians(:latitude))
                            *sin(radians(latitude)))
                LIMIT :limit OFFSET :offset
                """;

        // Create a Query object using the SQL query
        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("latitude", criteria.getLatitude());
        query.setParameter("longitude", criteria.getLongitude());
        query.setParameter("limit", criteria.getLimit());
        query.setParameter("offset", criteria.getOffset());

        // Execute the query and return the result list, with each result transformed into an OrderListViewDTO object
        return query.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new AliasToBeanResultTransformer(FlattenedBranchDTO.class))
                .getResultList();
    }
}
