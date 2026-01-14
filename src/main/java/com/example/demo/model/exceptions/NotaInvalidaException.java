package com.example.demo.model.exceptions;

public class NotaInvalidaException extends RuntimeException {
    public NotaInvalidaException(String message) {
        super(message);
    }
}