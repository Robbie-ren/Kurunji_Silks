package com.backend.order.exception;

import com.backend.common.exception.BusinessValidationException;

public class EmptyCartException extends BusinessValidationException {

    public EmptyCartException(String message) {
        super(message);
    }
}