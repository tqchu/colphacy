package com.colphacy.service;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.model.Product;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface ProductService {
    ProductDTO create(ProductDTO productDTO);

    ProductDTO update(ProductDTO productDTO);

    Product findById(Long id);

    ProductDTO findProductDTOById(Long id);

    List<ProductCustomerListViewDTO> getBestSellerProducts(int number);

    PageResponse<ProductAdminListViewDTO> getPaginatedProductsAdmin(String keyword, Integer categoryId, int offset, int limit);
}
