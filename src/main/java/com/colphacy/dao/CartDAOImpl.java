package com.colphacy.dao;

import com.colphacy.dto.cart.CartItemTuple;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CartDAOImpl implements CartDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void deleteByCustomerIdAndProductIdAndUnitId(List<CartItemTuple> values) {
        String sql = getdeleteByCustomerIdAndProductIdAndUnitIdQuery(values);
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }

    private String getdeleteByCustomerIdAndProductIdAndUnitIdQuery(List<CartItemTuple> values) {
        String sql = "DELETE FROM cart_item WHERE (customer_id, product_id, unit_id) IN (";
        for (int i = 0; i < values.size(); i++) {
            CartItemTuple cartItemTuple = values.get(i);
            sql += "(" + cartItemTuple.getCustomerId() + ", " + cartItemTuple.getProductId() + ", " + cartItemTuple.getUnitId() + ")";
            if (i < values.size() - 1) {
                sql += ", ";
            }
        }
        sql += ")";
        return sql;
    }

}
