package com.colphacy.controller;

import com.colphacy.dto.cart.CartDTO;
import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.dto.cart.CartItemUpdateDTO;
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
        cartItemService.addItemToCart(cartItemDTO, customer);
    }

    @Operation(summary = "Add products to cart", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("")
    public List<CartItemListViewDTO> addProductsToCart(@RequestBody @Valid CartDTO cartDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return cartItemService.addItemsToCart(cartDTO, customer);
    }

    @Operation(summary = "Update quantity of product in cart", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/update/{id}")
    public void updateQuantity(@PathVariable("id") Long cartId, @RequestBody @Valid CartItemUpdateDTO cartItemUpdateDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        cartItemService.updateQuantity(cartId, customer.getId(), cartItemUpdateDTO.getQuantity());
    }

    @Operation(summary = "Remove product from cart", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/remove/{id}")
    public void removeProductFromCart(@PathVariable("id") Long cartId, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        cartItemService.removeProductFromCart(cartId, customer.getId());
    }
}