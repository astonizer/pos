package com.increff.pos.util;

import com.increff.pos.service.exception.ApiException;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ConversionUtil {

    public static <Source, Target> Target convert(Source source, Class<Target> targetClass) throws ApiException {
        try {
            Target target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(target, source);
            return target;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public static <Source, Target> List<Target> convert(List<Source> sourceList, Class<Target> targetClass) throws ApiException {
        try {
            List<Target> targetList = new ArrayList<>();
            for(Source source: sourceList) {
                Target target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(target, source);
                targetList.add(target);
            }
            return targetList;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

}
