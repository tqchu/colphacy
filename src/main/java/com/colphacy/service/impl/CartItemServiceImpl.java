package com.colphacy.service.impl;

import com.colphacy.dto.cart.CartDTO;
import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.cart.CartItemListViewDTO;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    public void addItemToCart(CartItemDTO cartItemDTO, Customer customer) {
        addItem(customer, cartItemDTO);
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
    public void removeProductFromCart(Long cartId, Long customerId) {
        CartItem cartItem = cartItemRepository.findByIdAndCustomerId(cartId, customerId);

        if (cartItem == null) {
            throw new RecordNotFoundException("Đơn hàng không tồn tại");
        }
        cartItemRepository.deleteByIdAndCustomerId(cartId, customerId);
    }

    @Override
    public List<CartItemListViewDTO> addItemsToCart(CartDTO cartDTO, Customer customer) {
        List<CartItemListViewDTO> result = new ArrayList<>();
        cartDTO.getItems().forEach(item -> {
            result.add(cartItemMapper.cartItemToCartItemListViewDTO(addItem(customer, item)));
                }
        );
        return result;
    }

    private CartItem addItem(Customer customer, @Valid @NotNull CartItemDTO item) {
        Integer addedQuantity = item.getQuantity();
        Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy sản phẩm"));
        Unit unit = unitRepository.findById(item.getUnitId()).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy đơn vị"));
        ProductUnit productUnit = productUnitRepository.findByProductIdAndUnitId(product.getId(), unit.getId());
        if (productUnit == null) {
            throw new RecordNotFoundException("Sản phẩm không có đơn vị này");
        }
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductIdAndUnitId(customer.getId(), product.getId(), unit.getId());

        if (cartItem != null) {
            addedQuantity += cartItem.getQuantity();
            cartItem.setQuantity(addedQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
            cartItem.setQuantity(item.getQuantity());
            cartItem.setUnit(unit);
        }

        cartItemRepository.save(cartItem);
        return cartItem;
    }
}
