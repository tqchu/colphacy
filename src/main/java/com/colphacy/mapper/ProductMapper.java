package com.colphacy.mapper;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductSimpleDTO;
import com.colphacy.model.Product;
import com.colphacy.model.ProductImage;
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