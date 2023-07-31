package com.increff.pos.util;

import com.increff.pos.service.exception.ApiException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class NormalizeUtil {

    public static <T> void normalize(T object) throws ApiException {
        Class<?> classType = object.getClass();
        Field[] fields = classType.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType().getSimpleName().equals("String")) {
                field.setAccessible(true);
                try {
                    if (Objects.nonNull(field.get(object))) {
                        field.set(object, toLowerCase(field.get(object).toString()));
                    }
                } catch (IllegalAccessException e) {
                    throw new ApiException("There was an internal server error, please try again later");
                }
            } else if(field.getType().getSimpleName().equals("Double")) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(object);
                    if (Objects.nonNull(fieldValue)) {
                        // Round Double values to 2 decimal places
                        Double numericValue = (Double) fieldValue;
                        Double roundedValue = Math.round(numericValue * 100.0) / 100.0;
                        field.set(object, roundedValue);
                    }
                } catch (IllegalAccessException e) {
                    throw new ApiException("There was an internal server error, please try again later");
                }
            }
        }
    }

    public static <T> void normalize(List<T> objectList) throws ApiException {
        for(T object: objectList) {
            normalize(object);
        }
    }

    private static String toLowerCase(String string) {
        return Objects.isNull(string) ? null : string.trim().toLowerCase();
    }

}
