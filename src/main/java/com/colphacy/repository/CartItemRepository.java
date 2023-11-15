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

    @Query("SELECT c FROM cart_item c WHERE c.customer.id = :customerId and c.product.id = :productId")
    CartItem findByCustomerIdAndProductId(Long customerId, Long productId);

    @Query("UPDATE cart_item c SET c.quantity = :quantity WHERE c.product.id = :productId AND c.customer.id = :customerId")
    @Modifying
    @Transactional
    void updateQuantity(Long productId, Long customerId, Integer quantity);

    @Query("DELETE FROM cart_item c WHERE c.customer.id = :customerId and c.product.id = :productId")
    @Modifying
    @Transactional
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
}
