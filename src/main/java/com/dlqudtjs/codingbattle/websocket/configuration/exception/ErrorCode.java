package com.dlqudtjs.codingbattle.websocket.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    JSON_PARSE_ERROR(400, "JSON 파싱 에러");

    private final int status;
    private final String message;
}
