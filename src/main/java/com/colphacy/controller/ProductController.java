package com.colphacy.controller;

import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.service.ProductService;
import com.colphacy.validator.SaveProductValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SaveProductValidator saveProductValidator;

    @Value("${number-of-best-seller-products}")
    private int numberOfBestSellerProducts;

    @InitBinder
    public void initValidator(WebDataBinder binder) {
        binder.addValidators(saveProductValidator);
    }

    @Operation(summary = "Create a product", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.create(productDTO);
    }

    @Operation(summary = "Get product's detail by its id", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.findProductDTOById(id);
    }

    @Operation(summary = "Update a product", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping
    public ProductDTO updateProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.update(productDTO);
    }

    @Operation(summary = "Get best-seller products", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/best-sellers")
    public List<ProductCustomerListViewDTO> getBestSellerProducts() {
        return productService.getBestSellerProducts(numberOfBestSellerProducts);
    }
}
