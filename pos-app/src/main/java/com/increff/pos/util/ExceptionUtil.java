package com.increff.pos.util;

import com.google.gson.Gson;
import com.increff.pos.service.exception.ApiException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j
@Component
public class ExceptionUtil {

    /**
     * Converts list of error strings to JSON array
     *
     * @param errorList
     * @throws ApiException
     */
    public static void generateExceptionList(List<String> errorList) throws ApiException {
        throw new ApiException(new Gson().toJson(errorList));
    }

}
