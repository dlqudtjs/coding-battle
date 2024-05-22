package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSuccessCode {

    GAME_START_SUCCESS(200, "게임 시작 성공"),
    GAME_END_SUCCESS(200, "채점 시작"),
    ;

    private final int status;
    private final String message;
}
