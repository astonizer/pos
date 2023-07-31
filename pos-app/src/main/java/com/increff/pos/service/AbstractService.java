package com.increff.pos.service;

import com.increff.pos.service.exception.ApiException;

public abstract class AbstractService {

    protected void checkNotNull(Object object, String message) throws ApiException {
        if (object == null) {
            throw new ApiException(message);
        }
    }

    protected void checkNull(Object object, String message) throws ApiException {
        if (object != null) {
            throw new ApiException(message);
        }
    }

}
