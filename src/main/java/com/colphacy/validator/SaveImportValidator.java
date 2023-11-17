package com.colphacy.validator;


import com.colphacy.dto.imports.ImportDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SaveImportValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ImportDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ImportDTO anImport = (ImportDTO) target;
        Stream<String> compositeKeys = anImport.getImportDetails().stream()
                .map(detail -> detail.getProduct().getId() + "_" + detail.getUnitId() + "_" + detail.getExpirationDate());

        // Group by composite key and count occurrences
        Map<String, Long> compositeKeyCounts = compositeKeys
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Check if any composite key occurs more than once
        boolean hasDuplicateCompositeKey = compositeKeyCounts.values().stream().anyMatch(count -> count > 1);

        if (hasDuplicateCompositeKey) {
            errors.rejectValue("importDetails", "compositeKey.duplicate", "Không thể có các sản phẩm trùng lặp với cùng sản phẩm, đơn vị tính và ngày hết hạn");
        }

    }
}
