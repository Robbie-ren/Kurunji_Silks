package com.backend.order.exception;

import com.backend.common.exception.BusinessValidationException;

public class InvalidOrderStatusException extends BusinessValidationException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}