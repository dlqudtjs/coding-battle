package com.dlqudtjs.codingbattle.common.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
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

    @JsonCreator
    public static JudgeResultCode parsing(String input) {
        return Stream.of(JudgeResultCode.values())
                .filter(result -> result.name().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
