package com.colphacy.controller;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Create a product", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public ProductDTO createBranch(@Valid @RequestBody ProductDTO productDTO) {
        return productService.create(productDTO);
    }

    @Operation(summary = "Get product's detail by its id", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.findProductDTOById(id);
    }
}
