package com.backend.product.exception;

import com.backend.common.exception.BusinessValidationException;

public class InvalidProductException extends BusinessValidationException {

    public InvalidProductException(String message) {
        super(message);
    }
}