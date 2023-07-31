package com.increff.pos.util;

import com.increff.pos.service.exception.ApiException;
import javafx.util.Pair;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class ValidateUtil {

    public static <T> void validateForm(T form) throws ApiException {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        if (!violations.isEmpty()) {
            throw new ApiException(getValidationErrorMessage(violations));
        }
    }

    public static <T> void validateForm(List<T> formList) throws ApiException {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        List<String> errorList = new ArrayList<>();
        boolean hasError = false;
        for (T form : formList) {
            Set<ConstraintViolation<T>> violations = validator.validate(form);
            if (!violations.isEmpty()) {
                hasError = true;
                errorList.add(getValidationErrorMessage(violations));
            }
        }

        if (hasError) {
            ExceptionUtil.generateExceptionList(errorList);
        }
    }

    /**
     * This function is used to check for duplicate entries in a pojo list
     * @param checkDuplicateFunction Function to monitor how duplicate is checked
     */
    public static <T, P> void validateList(List<T> list, P collection, BiFunction<T, P, Pair<Boolean, String>> checkDuplicateFunction) throws ApiException {
        List<String> errorList = new ArrayList<>();
        boolean hasError = false;

        for (T pojo : list) {
            Pair<Boolean, String> result = checkDuplicateFunction.apply(pojo, collection);
            if (result.getKey()) {
                hasError = true;
            }
            errorList.add(result.getValue());
        }

        if (hasError) {
            ExceptionUtil.generateExceptionList(errorList);
        }
    }

    private static <T> String getValidationErrorMessage(Set<ConstraintViolation<T>> violations) {
        StringBuilder errorMessage = new StringBuilder();
        for (ConstraintViolation<T> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append(";");
        }
        return errorMessage.toString();
    }

}
