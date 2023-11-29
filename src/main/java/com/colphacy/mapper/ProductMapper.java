package com.colphacy.mapper;

import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductSimpleDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Product;
import com.colphacy.model.ProductImage;
import com.colphacy.model.ProductUnit;
import com.colphacy.model.Unit;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {IngredientMapper.class, ProductUnitMapper.class, CategoryMapper.class})
public interface ProductMapper {
    Product productDTOToProduct(ProductDTO productDTO);

    ProductDTO productToProductDTO(Product product);

    @Mapping(expression = "java(product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl())", target = "image")
    ProductSimpleDTO productToProductSimpleDTO(Product product);

    Product productSimpleDTOToProduct(ProductSimpleDTO product);

    @AfterMapping
    default void setImageUrl(@MappingTarget ProductSimpleDTO productSimpleDTO, Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            productSimpleDTO.setImage(product.getImages().get(0).getUrl());
        }
    }

    default ProductCustomerListViewDTO productToProductCustomerListViewDTO(Product product) {
        ProductCustomerListViewDTO res = new ProductCustomerListViewDTO();
        res.setName(product.getName());

        if (!product.getImages().isEmpty()) {
            res.setImage(product.getImages().get(0).getUrl());
        }

        if (!product.getProductUnits().isEmpty()) {
            res.setUnitName(product.getProductUnits().get(0).getUnit().getName());
            res.setSalePrice(product.getProductUnits().get(0).getSalePrice());
        }

        res.setId(product.getId());
        return res;
    }

    default ProductCustomerListViewDTO productAndUnitToProductCustomerListViewDTO(Product product, Unit unit) {
        ProductCustomerListViewDTO res = new ProductCustomerListViewDTO();
        res.setName(product.getName());

        if (!product.getImages().isEmpty()) {
            res.setImage(product.getImages().get(0).getUrl());
        }

        ProductUnit productUnit = product.getProductUnits().stream()
                .filter(pu -> pu.getUnit().equals(unit))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Không tìm thấy đơn vị"));

        res.setUnitName(productUnit.getUnit().getName());
        res.setSalePrice(productUnit.getSalePrice());

        res.setId(product.getId());
        return res;
    }

    default String fromProductImage(ProductImage productImage) {
        return productImage.getUrl();
    }

    default ProductImage fromProductImage(String url) {
        ProductImage productImage = new
                ProductImage();
        productImage.setUrl(url);
        return productImage;
    }
}