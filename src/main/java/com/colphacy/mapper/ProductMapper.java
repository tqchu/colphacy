package com.colphacy.mapper;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.model.Product;
import com.colphacy.model.ProductImage;
import com.colphacy.model.ProductUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {IngredientMapper.class, ProductUnitMapper.class})
public interface ProductMapper {
    @Mapping(source = "categoryId", target = "category.id")
    Product productDTOToProduct(ProductDTO productDTO);

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO productToProductDTO(Product product);


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

    default ProductAdminListViewDTO productToProductAdminListViewDTO(Product product) {
        ProductAdminListViewDTO res = new ProductAdminListViewDTO();
        res.setName(product.getName());
        res.setCategoryName(product.getCategory().getName());
        res.setId(product.getId());
        // Find main unit
        Optional<ProductUnit> maxRatioProductUnit = product.getProductUnits().stream()
                .max(Comparator.comparing(ProductUnit::getRatio));

        if (maxRatioProductUnit.isPresent()) {
            ProductUnit mainUnit = maxRatioProductUnit.get();
            res.setSalePrice(mainUnit.getSalePrice());
            res.setImportPrice(100000.0);
        }

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