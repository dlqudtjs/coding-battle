package com.dlqudtjs.codingbattle.common.exception.oauth;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
