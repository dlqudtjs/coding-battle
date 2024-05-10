package com.dlqudtjs.codingbattle.common.exception.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocketErrorCode {

    JSON_PARSE_ERROR(400, "JSON 파싱 에러");

    private final int status;
    private final String message;
}
