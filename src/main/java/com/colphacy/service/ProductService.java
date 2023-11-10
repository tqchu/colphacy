package com.colphacy.service;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.model.Product;

public interface ProductService {
    ProductDTO create(ProductDTO productDTO);

    ProductDTO update(ProductDTO productDTO);

    Product findById(Long id);

    ProductDTO findProductDTOById(Long id);
}
