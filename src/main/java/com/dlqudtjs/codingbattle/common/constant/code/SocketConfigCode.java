package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SocketConfigCode {
    NOT_CONNECT_USER("연결되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),

    JSON_PARSE_ERROR("JSON 파싱 에러", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
