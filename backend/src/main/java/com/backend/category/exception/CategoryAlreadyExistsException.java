package com.backend.category.exception;

import com.backend.common.exception.DuplicateResourceException;

public class CategoryAlreadyExistsException extends DuplicateResourceException {

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}