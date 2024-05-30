package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomConfigCode {

    CREATE_GAME_ROOM_SUCCESS(201, "대기방 생성 성공"),
    CREATE_GAME_ROOM_FAIL(400, "대기방 생성 실패"),
    JOIN_GAME_ROOM_SUCCESS(200, "대기방 입장 성공"),
    LEAVE_GAME_ROOM_SUCCESS(200, "대기방 퇴장"),
    GET_GAME_ROOM_LIST_SUCCESS(200, "대기방 목록 조회 성공"),
    INVALID_REQUEST(400, "올바르지 않은 요청입니다."),
    NOT_CONNECT_USER(400, "연결되지 않은 사용자입니다."),
    PASSWORD_NOT_MATCH(400, "비밀번호가 일치하지 않습니다."),
    NOT_EXIST_ROOM(400, "존재하지 않는 방입니다."),
    FULL_ROOM(400, "방이 가득 찼습니다."),
    NOT_EXIST_USER_IN_ROOM(400, "방에 존재하지 않는 사용자입니다."),
    SAME_USER_IN_ROOM(400, "같은 사용자가 방에 존재합니다.");

    private final int status;
    private final String message;
}
