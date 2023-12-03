package com.colphacy.mapper;

import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.CartItem;
import com.colphacy.model.Product;
import com.colphacy.model.ProductUnit;
import com.colphacy.model.Unit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    default CartItemListViewDTO cartItemToCartItemListViewDTO(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemListViewDTO cartItemListViewDTO = new CartItemListViewDTO();

        cartItemListViewDTO.setProductInfo( productAndUnitToProductCustomerListViewDTO( cartItem.getProduct(), cartItem.getUnit() ) );
        cartItemListViewDTO.setId( cartItem.getId() );
        cartItemListViewDTO.setQuantity( cartItem.getQuantity() );

        return cartItemListViewDTO;
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
        res.setUnitId(productUnit.getUnit().getId());
        res.setSalePrice(productUnit.getSalePrice());

        res.setId(product.getId());
        return res;
    }
}