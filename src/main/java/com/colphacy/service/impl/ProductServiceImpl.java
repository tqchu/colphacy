package com.colphacy.service.impl;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductUnitDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ProductMapper;
import com.colphacy.model.Product;
import com.colphacy.model.ProductStatus;
import com.colphacy.model.ProductUnit;
import com.colphacy.repository.IngredientRepository;
import com.colphacy.repository.ProductImageRepository;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.service.CategoryService;
import com.colphacy.service.ProductService;
import com.colphacy.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @Transactional
    @Override
    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO.getStatus() == ProductStatus.DISCONTINUED) {
            throw InvalidFieldsException.fromFieldError("status", "Không thể tạo sản phẩm ngừng kinh doanh");
        }
        categoryService.findById(productDTO.getCategoryId());
        productDTO.getProductUnits().forEach(productUnitDTO -> unitService.findById(productUnitDTO.getUnitId()));
        Product product = productMapper.productDTOToProduct(productDTO);
        product.setId(null);
        productRepository.save(product);
        return productMapper.productToProductDTO(product);
    }

    @Override
    public ProductDTO findProductDTOById(Long id) {
        return productMapper.productToProductDTO(findById(id));
    }


    @Transactional
    @Override
    public ProductDTO update(ProductDTO productDTO) {
        if (productDTO.getId() == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }

        Product existingProduct = findById(productDTO.getId());

        categoryService.findById(productDTO.getCategoryId());
        productDTO.getProductUnits().forEach(productUnitDTO -> unitService.findById(productUnitDTO.getUnitId()));
        Optional<ProductUnit> optionalBaseProductUnit = existingProduct.getProductUnits().stream()
                .filter(pUnit -> pUnit.getRatio().equals(1))
                .findFirst();

        Optional<ProductUnitDTO> optionalBaseProductUnitDto = productDTO.getProductUnits().stream()
                .filter(pUnit -> pUnit.getRatio().equals(1))
                .findFirst();

        if (optionalBaseProductUnit.isEmpty()) {
            throw new RuntimeException();
        }

        if (optionalBaseProductUnitDto.isEmpty()) {
            throw InvalidFieldsException.fromFieldError("productUnits", "Phải có 1 đơn vị tính có tỉ lệ quy đổi bằng 1");
        }

        if (!Objects.equals(optionalBaseProductUnitDto.get().getUnitId(), optionalBaseProductUnit.get().getUnit().getId())) {
            throw InvalidFieldsException.fromFieldError("productUnits", "Không được phép thay đổi đơn vị tính có tỉ lệ quy đổi bằng 1");
        }

        Product product = productMapper.productDTOToProduct(productDTO);
        ingredientRepository.deleteByProductId(product.getId());
        productUnitRepository.deleteByProductId(product.getId());
        productImageRepository.deleteByProductId(product.getId());
        productRepository.save(product);
        return productMapper.productToProductDTO(product);
    }

    @Override
    public Product findById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RecordNotFoundException("Không có sản phẩm tương ứng với id " + id);
        }
        return optionalProduct.get();
    }

}
