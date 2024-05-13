package com.dlqudtjs.codingbattle.common.exception;

import org.springframework.http.HttpStatus;

public class Custom4XXException extends RuntimeException {
    private final HttpStatus status;

    // 코드도 추가
    public Custom4XXException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
