package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoomConfigCode {

    CREATE_GAME_ROOM_SUCCESS("대기방 생성 성공", HttpStatus.CREATED),
    CREATE_GAME_ROOM_FAIL("대기방 생성 실패", HttpStatus.BAD_REQUEST),
    JOIN_GAME_ROOM_SUCCESS("대기방 입장 성공", HttpStatus.OK),
    LEAVE_GAME_ROOM_SUCCESS("대기방 퇴장", HttpStatus.OK),
    GET_GAME_ROOM_LIST_SUCCESS("대기방 목록 조회 성공", HttpStatus.OK),
    START_GAME_SUCCESS("게임 시작 성공", HttpStatus.OK),
    INVALID_REQUEST("올바르지 않은 요청입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_ROOM("존재하지 않는 방입니다.", HttpStatus.BAD_REQUEST),
    FULL_ROOM("방이 가득 찼습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER_IN_ROOM("방에 존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST),
    SAME_USER_IN_ROOM("같은 사용자가 방에 존재합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
