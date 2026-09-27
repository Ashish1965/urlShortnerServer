package com.example.urlshortner.exception;

public class NotValidUrlException extends RuntimeException {
    public NotValidUrlException(String message) {
        super(message);
    }
}