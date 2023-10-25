package com.colphacy.validator;

import com.colphacy.payload.request.ChangePasswordRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordRequest request = (ChangePasswordRequest) target;

        if (request.getNewPassword().equals(request.getOldPassword())) {
            errors.rejectValue("newPassword", "password.duplication", "Mật khẩu mới không được trùng mật khẩu cũ");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không trùng khớp.");
        }
    }
}