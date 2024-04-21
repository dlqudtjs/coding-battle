package com.dlqudtjs.codingbattle.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(400, "올바르지 않은 요청입니다.");

    private final int status;
    private final String message;
}
