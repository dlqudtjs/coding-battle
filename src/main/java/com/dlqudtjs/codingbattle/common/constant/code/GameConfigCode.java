package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameConfigCode {

    GAME_START_SUCCESS("게임 시작", HttpStatus.OK),
    GAME_END_SUCCESS("채점 시작", HttpStatus.OK),
    GAME_START_ERROR("게임을 시작할 수 없는 상황입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
