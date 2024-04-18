package com.dlqudtjs.codingbattle.service.room.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(400, "올바르지 않은 요청입니다."),
    NOT_CONNECT_USER(400, "연결되지 않은 사용자입니다."),
    NOT_EXIST_ROOM(400, "존재하지 않는 방입니다."),
    ;

    private final int status;
    private final String message;
}
