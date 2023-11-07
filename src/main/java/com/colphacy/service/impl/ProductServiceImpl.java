package com.colphacy.service.impl;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.mapper.ProductMapper;
import com.colphacy.model.Product;
import com.colphacy.repository.ProductRepository;
import com.colphacy.service.CategoryService;
import com.colphacy.service.ProductService;
import com.colphacy.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO create(ProductDTO productDTO) {
        categoryService.findById(productDTO.getCategoryId());
        productDTO.getProductUnits().forEach(productUnitDTO -> unitService.findById(productUnitDTO.getUnitId()));
        Product product = productMapper.productDTOToProduct(productDTO);
        product.setId(null);
        productRepository.save(product);
        return productMapper.productToProductDTO(product);
    }
}
