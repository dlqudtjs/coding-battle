package com.dlqudtjs.codingbattle.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode {

    INVALID_INPUT_VALUE("유효하지 않은 값입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
