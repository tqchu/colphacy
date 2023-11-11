package com.colphacy.validator;

import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductUnitDTO;
import com.colphacy.model.ProductStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SaveProductValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ProductDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductDTO product = (ProductDTO) target;

        long countRatioOne = product.getProductUnits().stream()
                .filter(productUnit -> productUnit.getRatio() == 1)
                .count();
        if (countRatioOne != 1) {
            errors.rejectValue("productUnits", "ratio.invalid", "Phải có duy nhất 1 tỉ lệ quy đổi có giá trị bằng 1");
        }

        long countDefaultUnit = product.getProductUnits().stream()
                .filter(ProductUnitDTO::isDefaultUnit)
                .count();
        if (countDefaultUnit == 0) {
            errors.rejectValue("productUnits", "isDefaultUnit.invalid", "Phải có ít nhất 1 đơn vị tính được đăng bán");
        }

        if (product.getStatus() == ProductStatus.FOR_SALE && (product.getImages() == null || product.getImages().isEmpty())) {
            errors.rejectValue("images", "image.required", "Phải thêm ảnh cho sản phẩm được đăng bán");
        }
    }
}
