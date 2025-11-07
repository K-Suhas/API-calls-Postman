package com.example.demo.ExceptionHandler;


public class InvalidMarksException extends RuntimeException {
    public InvalidMarksException(String message) {
        super(message);
    }
}

