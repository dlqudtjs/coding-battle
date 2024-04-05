package com.dlqudtjs.codingbattle.service.oauth.exception;

public class CustomMalformedJwtException extends RuntimeException {
    public CustomMalformedJwtException(String message) {
        super(message);
    }
}
