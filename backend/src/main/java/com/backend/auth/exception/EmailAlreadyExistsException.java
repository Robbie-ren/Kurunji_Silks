package com.backend.auth.exception;

import com.backend.common.exception.DuplicateResourceException;

public class EmailAlreadyExistsException extends DuplicateResourceException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}