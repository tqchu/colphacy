package com.colphacy.service.impl;

import com.colphacy.dto.cartItem.CartItemListViewDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CartItemMapper;
import com.colphacy.model.*;
import com.colphacy.repository.CartItemRepository;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.repository.UnitRepository;
import com.colphacy.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {
    private CartItemRepository cartItemRepository;

    private CartItemMapper cartItemMapper;

    private UnitRepository unitRepository;

    private ProductUnitRepository productUnitRepository;

    @Autowired
    public void setCartItemRepository(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Autowired
    public void setUnitRepository(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Autowired
    public void setProductUnitRepository(ProductUnitRepository productUnitRepository) {
        this.productUnitRepository = productUnitRepository;
    }

    @Autowired
    public void setCartItemMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<CartItemListViewDTO> findByCustomerId(Long customerId) {
        return cartItemRepository.findByCustomerId(customerId).stream().map(cartItemMapper::cartItemToCartItemListViewDTO).toList();
    }

    @Override
    public void addProduct(Long productId, Long unitId, Integer quantity, Customer customer) {
        Integer addedQuantity = quantity;
        Product product = productRepository.findById(productId).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy sản phẩm"));
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy đơn vị"));
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductIdAndUnitId(customer.getId(), product.getId(), unit.getId());
        ProductUnit productUnit = productUnitRepository.findByProductIdAndUnitId(product.getId(), unit.getId());
        if (productUnit == null) {
            throw new RecordNotFoundException("Sản phẩm không có đơn vị này");
        }

        if (cartItem != null) {
            addedQuantity += cartItem.getQuantity();
            cartItem.setQuantity(addedQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
            cartItem.setQuantity(quantity);
            cartItem.setUnit(unit);
        }

        cartItemRepository.save(cartItem);
    }

    @Override
    public void updateQuantity(Long cartId, Long customerId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByIdAndCustomerId(cartId, customerId);
        if (cartItem == null) {
            throw new RecordNotFoundException("Đơn hàng không tồn tại");
        }
        cartItemRepository.updateQuantity(cartId, customerId, quantity);
    }

    @Override
    public  void removeProductFromCart(Long cartId, Long customerId) {
        CartItem cartItem = cartItemRepository.findByIdAndCustomerId(cartId, customerId);

        if (cartItem == null) {
            throw new RecordNotFoundException("Đơn hàng không tồn tại");
        }
        cartItemRepository.deleteByIdAndCustomerId(cartId, customerId);
    }
}
