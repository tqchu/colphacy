package com.colphacy.controller;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductSearchCriteria;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.ProductService;
import com.colphacy.validator.SaveProductValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SaveProductValidator saveProductValidator;

    @Value("${number-of-best-seller-products}")
    private int numberOfBestSellerProducts;

    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @InitBinder("productDTO")
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


    @Operation(summary = "Get product's detail by its id for customers")
    @GetMapping("/customers/{id}")
    public ProductDTO getProductForCustomer(@PathVariable Long id) {
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


    @Operation(summary = "Get list of paginated products for admin", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<ProductAdminListViewDTO> getPaginatedProductsAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "Số bắt đầu phải là số không âm") int offset,
            @RequestParam(required = false)
            @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0") Integer limit,
            @RequestParam(required = false)
            String sortBy,
            @RequestParam(required = false)
            String order
    ) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return productService.getPaginatedProductsAdmin(keyword, categoryId, offset, limit, sortBy, order);
    }

    @Operation(summary = "Delete a product", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }


    @Operation(summary = "Get list of products with search and filter for customers", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/customers")
    public PageResponse<ProductCustomerListViewDTO> getPaginatedProductsCustomer(
            @Valid ProductSearchCriteria productSearchCriteria
    ) {
        if (productSearchCriteria.getLimit() == null) {
            productSearchCriteria.setLimit(defaultPageSize);
        }
        return productService.getPaginatedProductsCustomer(productSearchCriteria);
    }
}
