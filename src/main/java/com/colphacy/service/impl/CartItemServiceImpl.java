package com.colphacy.service.impl;

import com.colphacy.dto.cartItem.CartItemListViewDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CartItemMapper;
import com.colphacy.model.CartItem;
import com.colphacy.model.Customer;
import com.colphacy.model.Product;
import com.colphacy.repository.CartItemRepository;
import com.colphacy.repository.CustomerRepository;
import com.colphacy.repository.ProductRepository;
import com.colphacy.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {
    private CartItemRepository cartItemRepository;

    private CartItemMapper cartItemMapper;

    @Autowired
    public void setCartItemRepository(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
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
    public void addProduct(Long productId, Integer quantity, Customer customer) {
        Integer addedQuantity = quantity;
        Product product = productRepository.findById(productId).orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy sản phẩm"));;
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductId(customer.getId(), product.getId());

        if (cartItem != null) {
            addedQuantity += cartItem.getQuantity();
            cartItem.setQuantity(addedQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }

    @Override
    public void updateQuantity(Long productId, Long customerId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductId(customerId, productId);
        if (cartItem == null) {
            throw new RecordNotFoundException("Đơn hàng không tồn tại");
        }
        cartItemRepository.updateQuantity(productId, customerId, quantity);
    }

    @Override
    public  void removeProductFromCart(Long customerId, Long productId) {
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductId(customerId, productId);

        if (cartItem == null) {
            throw new RecordNotFoundException("Đơn hàng không tồn tại");
        }
        cartItemRepository.deleteByCustomerIdAndProductId(customerId, productId);
    }
}
