package com.colphacy.service.impl;

import com.colphacy.dao.ProductDAO;
import com.colphacy.dto.product.*;
import com.colphacy.enums.CustomerSearchViewSortField;
import com.colphacy.enums.SortOrder;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ProductMapper;
import com.colphacy.model.Product;
import com.colphacy.model.ProductStatus;
import com.colphacy.model.ProductUnit;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.IngredientRepository;
import com.colphacy.repository.ProductImageRepository;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.service.CategoryService;
import com.colphacy.service.ProductService;
import com.colphacy.service.UnitService;
import com.colphacy.types.PaginationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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
    private ProductDAO productDAO;
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

    @Override
    public List<ProductCustomerListViewDTO> getBestSellerProducts(int number) {
        Pageable pageable = PageRequest.of(0, number);
        return productRepository.findBestSellerProducts(pageable, ProductStatus.FOR_SALE).stream().map(productMapper::productToProductCustomerListViewDTO).toList();
    }

    @Override
    public PageResponse<ProductAdminListViewDTO> getPaginatedProductsAdmin(String keyword, Integer categoryId, int offset, int limit, String sortBy, String order) {
        if (sortBy != null && List.of("salePrice", "importPrice").contains(sortBy)) {
            if (sortBy.equals("salePrice")) {
                sortBy = "sale_price";
            }
            if (sortBy.equals("importPrice")) {
                sortBy = "import_price";
            }
        } else {
            sortBy = "id";
        }

        if (!Objects.equals(order, "desc")) {
            order = "asc";
        }

        PaginationRequest pageRequest = PaginationRequest.builder()
                .offset(offset)
                .limit(limit)
                .sortBy(sortBy)
                .order(order)
                .build();

        List<ProductAdminListViewDTO> list = productDAO.getPaginatedProductsAdmin(keyword, categoryId, pageRequest);
        Long totalItems = productDAO.getTotalProductsAdmin(keyword, categoryId);
        PageResponse<ProductAdminListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / limit) + 1);
        page.setLimit(limit);
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(offset);
        return page;
    }

    @Override
    public void delete(Long id) {
        findById(id);
        productRepository.deleteById(id);
    }

    @Override
    public PageResponse<ProductCustomerListViewDTO> getPaginatedProductsCustomer(ProductSearchCriteria productSearchCriteria) {
        // Validate categoryIds
        if (productSearchCriteria.getCategoryIds() != null) {
            productSearchCriteria.getCategoryIds().forEach(cId -> categoryService.findById(cId));
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (productSearchCriteria.getMinPrice() != null && productSearchCriteria.getMaxPrice() != null && productSearchCriteria.getMinPrice() > productSearchCriteria.getMaxPrice()) {
            throw InvalidFieldsException.fromFieldError("maxPrice", "Gía cao nhất không được nhỏ hơn giá thấp nhất");
        }

        // if sortBy != sale_price then set desc for review and sold
        if (!productSearchCriteria.getSortBy().toString().equalsIgnoreCase(CustomerSearchViewSortField.SALE_PRICE.toString())) {
            productSearchCriteria.setOrder(SortOrder.DESC);
        }

        List<ProductCustomerListViewDTO> list = productDAO.getPaginatedProductsCustomer(productSearchCriteria);

        Long totalItems = productDAO.getTotalProductsCustomer(productSearchCriteria);
        PageResponse<ProductCustomerListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / productSearchCriteria.getLimit()) + 1);
        page.setLimit(productSearchCriteria.getLimit());
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(productSearchCriteria.getOffset());
        return page;
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
