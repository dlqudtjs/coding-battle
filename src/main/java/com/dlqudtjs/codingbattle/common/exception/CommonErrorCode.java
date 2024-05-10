package com.dlqudtjs.codingbattle.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode {

    INVALID_INPUT_VALUE(400, "유효하지 않은 값입니다."),
    ;

    private final int status;
    private final String message;
}
