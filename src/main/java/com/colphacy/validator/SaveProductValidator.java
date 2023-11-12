package com.colphacy.validator;

import com.colphacy.dto.product.IngredientDTO;
import com.colphacy.dto.product.ProductDTO;
import com.colphacy.dto.product.ProductUnitDTO;
import com.colphacy.model.ProductStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SaveProductValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ProductDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductDTO product = (ProductDTO) target;

        // Check if there are any product unit duplicates by field unitId
        Map<Long, Long> unitIdCounts = product.getProductUnits().stream()
                .collect(Collectors.groupingBy(ProductUnitDTO::getUnitId, Collectors.counting()));

        boolean hasDuplicateUnitId = unitIdCounts.values().stream().anyMatch(count -> count > 1);

        if (hasDuplicateUnitId) {
            errors.rejectValue("productUnits", "unitId.duplicate", "Không thể có các đơn vị sản phẩm trùng lặp");
        }

        // Check if there are product unit any duplicates by field ratio
        Map<Integer, Long> unitRatioCounts = product.getProductUnits().stream()
                .collect(Collectors.groupingBy(ProductUnitDTO::getRatio, Collectors.counting()));

        boolean hasDuplicateUnitRatio = unitRatioCounts.values().stream().anyMatch(count -> count > 1);

        if (hasDuplicateUnitRatio) {
            errors.rejectValue("productUnits", "ratio.duplicate", "Không thể có các đơn vị tính trùng tỉ lệ quy đổi");
        }

        // Check if there are any ingredient duplicates
        Map<String, Long> ingredientCounts = product.getIngredients().stream()
                .collect(Collectors.groupingBy(IngredientDTO::getName, Collectors.counting()));

        boolean hasDuplicateIngredient = ingredientCounts.values().stream().anyMatch(count -> count > 1);

        if (hasDuplicateIngredient) {
            errors.rejectValue("ingredients", "duplicate", "Không thể có các thành phần trùng lặp");
        }

        // Check if there are any duplicates by field ratio
        Map<String, Long> productImageCounts = product.getImages().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        boolean hasDuplicateImages = productImageCounts.values().stream().anyMatch(count -> count > 1);

        if (hasDuplicateImages) {
            errors.rejectValue("images", "duplicate", "Không thể có các ảnh trùng lặp");
        }

        // Check if there are any over one element has ratio = 1
        long countRatioOne = product.getProductUnits().stream()
                .filter(productUnit -> productUnit.getRatio() == 1)
                .count();
        if (countRatioOne != 1) {
            errors.rejectValue("productUnits", "ratio.invalid", "Phải có duy nhất 1 tỉ lệ quy đổi có giá trị bằng 1");
        }

        // Check if there are no element for sale

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
