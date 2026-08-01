package com.backend.product.exception;

import com.backend.common.exception.BusinessValidationException;

public class InsufficientStockException extends BusinessValidationException {

    public InsufficientStockException(String message) {
        super(message);
    }
}