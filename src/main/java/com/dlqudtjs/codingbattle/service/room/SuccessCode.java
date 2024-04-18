package com.dlqudtjs.codingbattle.service.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    CREATE_WAIT_ROOM_SUCCESS(201, "대기방 생성 성공"),
    ;

    private final int status;
    private final String message;
}
