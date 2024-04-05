package com.dlqudtjs.codingbattle.service.oauth.exception;

public class CustomUnsupportedJwtException extends RuntimeException {
    public CustomUnsupportedJwtException(String message) {
        super(message);
    }
}
