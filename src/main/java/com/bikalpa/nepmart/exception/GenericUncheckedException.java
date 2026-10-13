package com.bikalpa.nepmart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
public class GenericUncheckedException extends RuntimeException {

    private static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";

    private final String errorCode;
    private final HttpStatus status;

    public GenericUncheckedException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public GenericUncheckedException(String resourceName, Integer id) {
        super(String.format("%s not found with id: %s", resourceName, id));
        this.errorCode = RESOURCE_NOT_FOUND;
        this.status = HttpStatus.NOT_FOUND;
    }

    public GenericUncheckedException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}