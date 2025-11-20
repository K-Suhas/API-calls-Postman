package com.example.demo.ExceptionHandler;

public class MailGatewayException extends RuntimeException {
    public MailGatewayException(String message) { super(message); }
}