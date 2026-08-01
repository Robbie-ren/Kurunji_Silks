package com.backend.product.exception;

import com.backend.common.exception.DuplicateResourceException;

public class DuplicateProductException extends DuplicateResourceException {

    public DuplicateProductException(String message) {
        super(message);
    }
}