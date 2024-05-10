package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomSuccessCode {

    CREATE_GAME_ROOM_SUCCESS(201, "대기방 생성 성공"),
    JOIN_GAME_ROOM_SUCCESS(200, "대기방 입장 성공"),
    LEAVE_GAME_ROOM_SUCCESS(200, "대기방 퇴장"),
    GET_GAME_ROOM_LIST_SUCCESS(200, "대기방 목록 조회 성공"),
    ;

    private final int status;
    private final String message;
}
