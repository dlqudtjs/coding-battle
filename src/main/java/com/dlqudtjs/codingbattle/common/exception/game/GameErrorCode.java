package com.dlqudtjs.codingbattle.common.exception.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameErrorCode {

    GAME_START_ERROR("게임을 시작할 수 없는 상황입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
