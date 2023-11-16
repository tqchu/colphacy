package com.colphacy.dao;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductSearchCriteria;
import com.colphacy.types.PaginationRequest;

import java.util.List;

public interface ProductDAO {
    List<ProductAdminListViewDTO> getPaginatedProductsAdmin(String keyword, Integer categoryId, PaginationRequest paginationRequest);

    Long getTotalProductsAdmin(String keyword, Integer categoryId);

    List<ProductCustomerListViewDTO> getPaginatedProductsCustomer(ProductSearchCriteria productSearchCriteria);

    Long getTotalProductsCustomer(ProductSearchCriteria criteria);
}
