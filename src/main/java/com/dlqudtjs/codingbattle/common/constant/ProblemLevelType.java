package com.dlqudtjs.codingbattle.common.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProblemLevelType {
    BRONZE1(1),
    BRONZE2(2),
    BRONZE3(3),
    BRONZE4(4),
    BRONZE5(5),
    SILVER1(6),
    SILVER2(7),
    SILVER3(8),
    SILVER4(9),
    SILVER5(10),
    ;

    private final int value;

    @JsonCreator
    public static ProblemLevelType parsing(int input) {
        return Stream.of(ProblemLevelType.values())
                .filter(level -> level.getValue() == input)
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
