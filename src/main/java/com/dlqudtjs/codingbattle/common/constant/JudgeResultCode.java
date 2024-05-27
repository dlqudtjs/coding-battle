package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JudgeResultCode {
    PENDING(0),
    PASS(1),
    FAIL(2),
    ERROR(3);

    private final int code;
}
