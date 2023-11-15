package com.colphacy.controller;

import com.colphacy.dto.cartItem.CartItemDTO;
import com.colphacy.dto.cartItem.CartItemListViewDTO;
import com.colphacy.model.Customer;
import com.colphacy.service.CartItemService;
import com.colphacy.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController()
@RequestMapping("api/carts")
public class CartController {
    private CartItemService cartItemService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    public void setCartItemService(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @Operation(summary = "Get a list of cart items", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping()
    public List<CartItemListViewDTO> getListCartItem(Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return cartItemService.findByCustomerId(customer.getId());
    }

    @Operation(summary = "Add product to cart", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/add")
    public void addProductToCart(@RequestBody @Valid CartItemDTO cartItemDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        cartItemService.addProduct(cartItemDTO.getProductId(), cartItemDTO.getQuantity(), customer);
    }

    @Operation(summary = "Update quantity of product in cart", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/update")
    public void updateQuantity(@RequestBody @Valid CartItemDTO cartItemDTO,  Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        cartItemService.updateQuantity(cartItemDTO.getProductId(), customer.getId(), cartItemDTO.getQuantity());
    }

    @Operation(summary = "Remove product from cart", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/remove/{product_id}")
    public void removeProductFromCart(@PathVariable("product_id") Long productId,  Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        cartItemService.removeProductFromCart(customer.getId(), productId);
    }
}
