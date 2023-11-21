package com.colphacy.repository;

import com.colphacy.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM cart_item c WHERE c.customer.id = :customerId")
    List<CartItem> findByCustomerId(Long customerId);

    @Query("SELECT c FROM cart_item c WHERE c.customer.id = :customerId and c.product.id = :productId and c.unit.id = :unitId")
    CartItem findByCustomerIdAndProductIdAndUnitId(Long customerId, Long productId, Long unitId);

    @Query("UPDATE cart_item c SET c.quantity = :quantity WHERE c.customer.id = :customerId AND c.id = :cartId")
    @Modifying
    @Transactional
    void updateQuantity(Long cartId, Long customerId, Integer quantity);

    @Query("DELETE FROM cart_item c WHERE c.id = :id and c.customer.id = :customerId")
    @Modifying
    @Transactional
    void deleteByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT c FROM cart_item c WHERE c.id = :id and c.customer.id = :customerId")
    CartItem findByIdAndCustomerId(Long id, Long customerId);

    @Modifying
    @Query("DELETE FROM cart_item c WHERE c.product.id IN :productIds AND c.unit.id IN :unitIds AND c.customer.id = :customerId")
    @Transactional
    void deleteByProductIdsAndUnitIdsAndCustomerId(List<Long> productIds, List<Long> unitIds, Long customerId);
}
