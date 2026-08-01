package com.backend.image.exception;

import com.backend.common.exception.BusinessValidationException;

public class ImageUploadException extends BusinessValidationException {

    public ImageUploadException(String message) {
        super(message);
    }
}