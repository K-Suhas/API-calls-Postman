package com.example.demo.ExceptionHandler;

import java.util.List;

public class BulkValidationException extends RuntimeException {
    private final List<String> errors;

    public BulkValidationException(List<String> errors) {
        super("Bulk validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

