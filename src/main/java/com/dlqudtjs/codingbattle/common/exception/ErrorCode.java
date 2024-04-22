package com.dlqudtjs.codingbattle.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    LANGUAGE_NOT_FOUND(400, "언어를 찾을 수 없습니다."),
    ;

    private final int status;
    private final String message;
}
