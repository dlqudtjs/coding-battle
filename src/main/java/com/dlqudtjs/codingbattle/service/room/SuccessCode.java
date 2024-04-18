package com.dlqudtjs.codingbattle.service.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    CREATE_WAIT_ROOM_SUCCESS(201, "대기방 생성 성공"),
    JOIN_WAIT_ROOM_SUCCESS(200, "대기방 입장 성공"),
    LEAVE_WAIT_ROOM_SUCCESS(200, "대기방 퇴장"),
    ;

    private final int status;
    private final String message;
}
