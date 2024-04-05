package com.dlqudtjs.codingbattle.service.oauth.exception;

public class CustomExpiredJwtException extends RuntimeException {
    public CustomExpiredJwtException(String message) {
        super(message);
    }
}
