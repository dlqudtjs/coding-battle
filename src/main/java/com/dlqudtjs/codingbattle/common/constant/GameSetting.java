package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSetting {
    DEFAULT_ROOM_MAX_USER_COUNT(999),
    MIN_USER_COUNT(2),
    MAX_USER_COUNT(4),
    MIN_PROBLEM_LEVEL(1),
    MAX_PROBLEM_LEVEL(10),
    MIN_SUBMISSION_COUNT(1),
    MAX_SUBMISSION_COUNT(10),
    MIN_LIMIT_TIME(10),
    MAX_LIMIT_TIME(120),
    GAME_START_MIN_USER_COUNT(2);

    private final int value;
}
