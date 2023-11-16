package com.colphacy.dto.product;

import com.colphacy.enums.CustomerSearchViewSortField;
import com.colphacy.enums.SortOrder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProductSearchCriteria {
    private String keyword;
    @Size(min = 1)
    private List<@Min(value = 1, message = "Id danh mục không hợp lệ") Long> categoryIds;
    @Min(value = 0, message = "Giá sản phẩm không thể âm")
    private Integer minPrice;
    @Min(value = 0, message = "Giá sản phẩm không thể âm")
    private Integer maxPrice;
    private CustomerSearchViewSortField sortBy = CustomerSearchViewSortField.SOLD;
    private SortOrder order = SortOrder.DESC;
    @Min(value = 0, message = "Số bắt đầu phải là số không âm")
    private Integer offset;
    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
}
