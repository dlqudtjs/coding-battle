package com.dlqudtjs.codingbattle.common.exception.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomErrorCode {

    INVALID_REQUEST(400, "올바르지 않은 요청입니다."),
    NOT_CONNECT_USER(400, "연결되지 않은 사용자입니다."),
    NOT_EXIST_ROOM(400, "존재하지 않는 방입니다."),
    FULL_ROOM(400, "방이 가득 찼습니다."),
    NOT_EXIST_USER_IN_ROOM(400, "방에 존재하지 않는 사용자입니다."),
    SAME_USER_IN_ROOM(400, "같은 사용자가 방에 존재합니다.");

    private final int status;
    private final String message;
}