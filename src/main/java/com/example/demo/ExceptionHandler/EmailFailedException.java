package com.example.demo.ExceptionHandler;

public class EmailFailedException extends RuntimeException{
    public EmailFailedException(String message) {
        super(message);
    }
}
