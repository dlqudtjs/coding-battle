package com.dlqudtjs.codingbattle.common.exception.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SocketErrorCode {

    JSON_PARSE_ERROR("JSON 파싱 에러", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
